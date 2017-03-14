# ruby civsim3.rb "desired_decisions_folder"

# globals declaration
$folder="civ3brains/"+ARGV[0]+"/"
$resources=23
$maxLoyalty=5
$step=0.01
$rand=Random.new
$decisions=[]
$synapses=[]
$synSets=100
$guesses=[]

# class declaration
class Decision
  def initialize file
    correct=[]
    File.open(file).each_with_index do |line,index|
      if index==0
        correct=line.split ","
        correct=[correct[0].to_i,correct[1].to_i]
        @correct=correct
      else
        @data=eval(line)
        @data[0]=[@data[0]]
      end
    end
    setOptions correct
    #puts options.length.to_s+"="+@data[0].length.to_s+"+"+@data[1].length.to_s+"+"+@data[2].length.to_s+"+"+@data[3].length.to_s
  end

  def data
    return @data
  end
  def options
    return @options
  end
  def correct
    return @correct
  end
  def getHighest
    return @highest
  end

  def setOptions correct
    @options=[[Option.new(self,0,0)]]
    for a in 0...@data[1].length do
      @options.push [Option.new(self,1+a,0),Option.new(self,1+a,1)]
    end
    for a in 0...@data[2].length do
      @options.push [Option.new(self,1+@data[1].length+a,0)]
    end
    for a in 0...@data[3].length do
      @options.push [Option.new(self,1+@data[1].length+@data[2].length+a,0)]
    end
    @options[correct[0]][correct[1]].setExpected 1.0
  end

  def isCorrect?
    highest=[nil,nil]
    for a in 0...options.length do
      for b in 0...options[a].length do
        if highest[0]==nil || options[a][b].value>options[highest[0]][highest[1]].value
          highest=[a,b]
        end
      end
    end
    @highest=highest
    return options[highest[0]][highest[1]].expected==1.0
  end
end
class Option
  def initialize decision,index,secIndex
    @decision=decision
    @secIndex=secIndex
    @expected=0.0
    if isCity index
      @type=0
    elsif isNeighbor @decision.data,index
      @type=1
    elsif isAlly @decision.data,index
      @type=2
    else
      @type=3
    end
  end
  def setExpected num
    @expected=num
  end
  def expected
    return @expected
  end
  def value
    return @value
  end

  def cityCoefficients cities,syn,syn1,synIndex
    co=[]
    for a in 0...$synapses[syn][syn1].length do
      list=[]
      for b in 0...$synapses[syn][syn1][a].length do
        list.push 0
      end
      co.push list
    end
    for city in cities do
      for a in 0...city.length do
        for b in 0...city[a].length do
          co[a][b]+=city[a][b]
          @coefSum+=city[a][b]**2
        end
      end
    end
    @coefficients.push co
    addValue co,$synapses[syn][syn1],synIndex
  end
  def plotCoefficients plots,synIndex
    co=[]
    for a in 0...$synapses[3][0].length do
      list=[]
      for b in 0...$synapses[3][0][a].length do
        list.push 0
      end
      co.push list
    end
    for plot in plots do
      for a in 0...plot.length do
        for b in 0...plot[a].length do
          co[a][b]+=plot[a][b]
          @coefSum+=plot[a][b]**2
        end
      end
    end
    @coefficients.push co
    addValue co,$synapses[3][0],synIndex
  end

  def addValue coefficients,synapses,synIndex
    for a in 0...coefficients.length do
      for b in 0...coefficients[a].length do
        @value+=coefficients[a][b]*synapses[a][b][synIndex]
      end
    end
  end
  def recalcValue synIndex
    @coefficients=[]
    @coefSum=0.0
    @value=0.0
    if @type==0
      cityCoefficients @decision.data[0],0,0,synIndex
    elsif @type==1
      if @secIndex==0
        cityCoefficients @decision.data[0],0,1,synIndex
        cityCoefficients @decision.data[1],1,0,synIndex
      else
        cityCoefficients @decision.data[0],0,2,synIndex
        cityCoefficients @decision.data[1],1,1,synIndex
      end
    elsif @type==2
      cityCoefficients @decision.data[0],0,3,synIndex
      cityCoefficients @decision.data[1],1,2,synIndex
      cityCoefficients @decision.data[2],2,0,synIndex
    else
      cityCoefficients @decision.data[0],0,4,synIndex
      cityCoefficients @decision.data[1],1,3,synIndex
      cityCoefficients @decision.data[2],2,1,synIndex
      plotCoefficients @decision.data[3],synIndex
    end
  end
  def changeSynapses sign,synIndex
    if @type==0
      change sign,0,0,synIndex
    elsif @type==1
      if @secIndex==0
        change sign,0,1,synIndex
        change sign,1,0,synIndex
      else
        change sign,0,2,synIndex
        change sign,1,1,synIndex
      end
    elsif @type==2
      change sign,0,3,synIndex
      change sign,1,2,synIndex
      change sign,2,0,synIndex
    else
      change sign,0,4,synIndex
      change sign,1,3,synIndex
      change sign,2,1,synIndex
      change sign,3,0,synIndex
    end
  end
  def change sign,syn,syn1,synIndex
    for a in 0...$synapses[syn][syn1].length do
      for b in 0...$synapses[syn][syn1][a].length do
        $synapses[syn][syn1][a][b][synIndex]+=sign*$step*(@coefficients[syn][a][b]/Math.sqrt(@coefSum))
      end
    end
  end
end

# method declaration
def randomBrain
  $synapses.push [citySynapses,citySynapses,citySynapses,citySynapses,citySynapses]
  $synapses.push [citySynapses,citySynapses,citySynapses,citySynapses]
  $synapses.push [citySynapses,citySynapses]
  $synapses.push [plotSynapses]
end

def citySynapses
  synapses=[]
  for a in 0...$resources*2 do
    synapses.push [randSynapse]
  end

  array=[]
  for a in 0...$maxLoyalty+1 do
    array.push randSynapse
  end
  synapses.push array

  array=[]
  for a in 0...15 do
    array.push randSynapse
  end
  synapses.push array

  array=[]
  array1=[]
  for a in 0...20 do
    array.push randSynapse
    array1.push randSynapse
  end
  synapses.push array
  synapses.push array1

  synapses.push [randSynapse,randSynapse]
  return synapses
end

def plotSynapses
  synapses=[]
  for a in 0...$resources do
    synapses.push [randSynapse]
  end

  array=[]
  for a in 0...20 do
    array.push randSynapse
  end
  synapses.push array
  return synapses
end

def randSynapse
  syn=[]
  for a in 0...$synSets do
    syn.push $rand.rand(0.02)-0.01
  end
  return syn
end

def isCity index
  return index==0
end
def isNeighbor data,index
  return index>0 && index<=data[1].length
end
def isAlly data,index
  return index>data[1].length && index<=data[1].length+data[2].length
end
def isPlot data,index
  return index>data[1].length+data[2].length
end

def fitToDecision dec
  synIndex=0
  for l in dec.options do
    #synIndex=0
    for option in l do
      option.recalcValue synIndex
      diff=option.expected-option.value
      option.changeSynapses diff/3,synIndex
      #if diff>0
      #  option.changeSynapses 1,synIndex
      #elsif diff<0
      #  option.changeSynapses -1,synIndex
      #end
      synIndex+=1
      if synIndex==$synSets
        synIndex=0
      end
    end
  end
  return dec.isCorrect?
end
def logGuess b
  $guesses.push b
  if $guesses.length>200
    $guesses.slice! 0
  end
  total=0
  for a in $guesses do
    if a==true
      total+=1
    end
  end
  return total*100/$guesses.length
end

# initialization
puts "Loading decision files..."
Dir.entries($folder).each do |file|
  if File.extname(file)==".dec"
    $decisions.push Decision.new($folder+file)
  end
end
puts "All "+$decisions.length.to_s+" decisions loaded"
puts "Initiating brain generation..."
randomBrain
puts "Crude synapse network generated"
puts "Initiating network development. Use ctrl+C to terminate."
loopCount=0
index=0
percent=0
while percent<95 do
  result=fitToDecision $decisions[index]
  percent=logGuess(result)
  print "\rloop #"+loopCount.to_s+": "+percent.to_s+"% accurate                 "
  loopCount+=1
  index+=1
  if index>=$decisions.length
    index=0
  end
end

def printCompletion index
  print "\r"+index.to_s+"/"+$synapses.length.to_s+" completion..."
end

puts "\nNeural network development complete"
puts "Converting brain to type string"

brain="[\n"
printCompletion 0
for a in 0...$synapses.length do
  brain+="[\n"
  for b in 0...$synapses[a].length do
    brain+="[\n"
    for c in 0...$synapses[a][b].length do
      brain+="[\n"
      for d in 0...$synapses[a][b][c].length do
        brain+="[\n"
        for e in 0...$synapses[a][b][c][d].length do
          brain+=$synapses[a][b][c][d][e].to_s+"\n"
          #if e<$synapses[a][b][c][d].length-1
          #  brain+=","
          #end
        end
        brain+="]\n"
        #if d<$synapses[a][b][c].length-1
        #  brain+=","
        #end
        #brain+="\n"
      end
      brain+="]\n"
      #if c<$synapses[a][b].length-1
      #  brain+=","
      #end
      #brain+="\n"
    end
    brain+="]\n"
    #if b<$synapses[a].length-1
    #  brain+=","
    #end
    #brain+="\n"
  end
  brain+="]\n"
  #if a<$synapses.length-1
  #  brain+=","
  #end
  #brain+="\n"
  printCompletion a+1
end
brain+="]"

file=File.new "brains/"+ARGV[0]+".brain","w+"
puts "\nWriting brain to file..."
file.puts brain
brain=""
file.close
puts "Brain file successfully written to file"
