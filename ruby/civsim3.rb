# ruby civsim3.rb "desired_decisions_folder"

# globals declaration
$folder="civ3brains/"+ARGV[0]+"/"
$resources=23
$maxLoyalty=5
$step=0.01
$rand=Random.new
$decisions=[]
$synapses=[]
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

  def cityCoefficients cities,syn,syn1
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
    addValue co,$synapses[syn][syn1]
  end
  def plotCoefficients plots
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
    addValue co,$synapses[3][0]
  end

  def addValue coefficients,synapses
    for a in 0...coefficients.length do
      for b in 0...coefficients[a].length do
        @value+=coefficients[a][b]*synapses[a][b]
      end
    end
  end
  def recalcValue
    @coefficients=[]
    @coefSum=0.0
    @value=0.0
    if @type==0
      cityCoefficients @decision.data[0],0,0
    elsif @type==1
      if @secIndex==0
        cityCoefficients @decision.data[0],0,1
        cityCoefficients @decision.data[1],1,0
      else
        cityCoefficients @decision.data[0],0,2
        cityCoefficients @decision.data[1],1,1
      end
    elsif @type==2
      cityCoefficients @decision.data[0],0,3
      cityCoefficients @decision.data[1],1,2
      cityCoefficients @decision.data[2],2,0
    else
      cityCoefficients @decision.data[0],0,4
      cityCoefficients @decision.data[1],1,3
      cityCoefficients @decision.data[2],2,1
      plotCoefficients @decision.data[3]
    end
  end
  def changeSynapses sign
    if @type==0
      change sign,0,0
    elsif @type==1
      if @secIndex==0
        change sign,0,1
        change sign,1,0
      else
        change sign,0,2
        change sign,1,1
      end
    elsif @type==2
      change sign,0,3
      change sign,1,2
      change sign,2,0
    else
      change sign,0,4
      change sign,1,3
      change sign,2,1
      change sign,3,0
    end
  end
  def change sign,syn,syn1
    for a in 0...$synapses[syn][syn1].length do
      for b in 0...$synapses[syn][syn1][a].length do
        $synapses[syn][syn1][a][b]+=sign*$step*(@coefficients[syn][a][b]/Math.sqrt(@coefSum))
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
  return $rand.rand(0.02)-0.01
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
  for l in dec.options do
    for option in l do
      option.recalcValue
      diff=option.expected-option.value
      option.changeSynapses diff
      #if diff>0
      #  option.changeSynapses 1
      #elsif diff<0
      #  option.changeSynapses -1
      #end
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
puts "Initiating network development..."
a=300
index=0
while a>0 do
  result=fitToDecision $decisions[index]
  print "\r"+(a-1).to_s+" loops remaining: "+logGuess(result).to_s+"% accurate      "
  a-=1
  index+=1
  if index>=$decisions.length
    index=0
  end
end
puts "\nAll done"
