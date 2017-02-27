# ruby civsim3.rb "desired_decisions_folder"

# globals declaration
$folder="civ3brains/"+ARGV[0]+"/"
$resources=23
$cityVarLength=($resources*2)+5
$plotVarLength=$resources+1
$maxLoyalty=5
$step=0.001
$rand=Random.new
$decisions=[]
$synapses=[]

# class declaration
class Decision
  def initialize file
    correct=[]
    File.open(file).each_with_index do |line,index|
      if index==0
        correct=line.split ","
      else
        @data=eval(line)
      end
    end
    setOptions correct
  end

  def data
    return @data
  end
  def options
    return @options
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
    @options[correct[0].to_i][correct[1].to_i].setExpected 1
  end
end
class Option
  def initialize decision,index,secIndex
    @decision=decision
    @secIndex=secIndex
    @expected=0
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
    for a in 0...$cityVarLength do
      co.push 0
    end
    for city in cities do
      for a in 0...$resources*2 do
        co[a]+=city[a]
      end
      for a in $resources*2...$cityVarLength do
        co[a]+=$synapses[syn][syn1][a][city[a]]
      end
    end
    @coefficients.push co
    addValue co,$synapses[syn][syn1]
  end
  def plotCoefficients plots
    co=[]
    for a in 0...$plotVarLength do
      co.push 0
    end
    for plot in plots do
      for a in 0...$resources do
        co[a]+=plot[a]
      end
      for a in $resources...$plotVarLength do
        co[a]+=$synapses[3][0][a][plot[a]]
      end
    end
    @coefficients.push co
    addValue co,$synapses[3][0]
  end

  def addValue coefficients,synapses
    for a in 0...coefficients.length do
      if synapses[a].length==1#kind_of? Array
        @value+=coefficients[a]
      elsif
        @value+=coefficients[a]*synapses[a][0]
      end
    end
  end
  def recalcValue
    @coefficients=[]
    @value=0
    if @type==0
      cityCoefficients [@decision.data[0]],0,0
    elsif @type==1
      if @secIndex==0
        cityCoefficients [@decision.data[0]],0,1
        cityCoefficients @decision.data[1],1,0
      else
        cityCoefficients [@decision.data[0]],0,2
        cityCoefficients @decision.data[1],1,1
      end
    elsif @type==2
      cityCoefficients [@decision.data[0]],0,3
      cityCoefficients @decision.data[1],1,2
      cityCoefficients @decision.data[2],2,0
    else
      cityCoefficients [@decision.data[0]],0,4
      cityCoefficients @decision.data[1],1,3
      cityCoefficients @decision.data[2],2,1
      plotCoefficients @decision.data[3]
    end
  end
  def changeSynapses sign
    changes=[]
    sum=0
    for a in 0...@coefficients.length do
      list=[]
      for b in 0...@coefficients[a].length do
        crude=sign*$step/@coefficients[a][b]
        list.push crude
        sum+=crude
      end
      changes.push list
    end

    if @type==0
      incrementChange changes,0,0,sum
    elsif @type==1
      if @secIndex==0
        incrementChange changes,0,1,sum
        incrementChange changes,1,0,sum
      else
        incrementChange changes,0,2,sum
        incrementChange changes,1,1,sum
      end
    elsif @type==2
      incrementChange changes,0,3,sum
      incrementChange changes,1,2,sum
      incrementChange changes,2,0,sum
    else
      incrementChange changes,0,4,sum
      incrementChange changes,1,3,sum
      incrementChange changes,2,1,sum
      incrementChange changes,3,0,sum
    end
  end
  def incrementChange changes,syn,syn1,sum
    for a in 0...$synapses[syn][syn1].length do
      l=$synapses[syn][syn1][a].length
      for b in 0...l do
        $synapses[syn][syn1][a][b]+=changes[syn][a]*$step/(sum*l)
      end
      #if $synapses[syn][syn1][a].length==1
      #  $synapses[syn][syn1][a][0]+=changes[syn][a]*$step/sum
      #else
      #  #$synapses[syn][syn1][a][???]+=changes[syn][a]*$step/sum
      #end
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
  return $rand.rand 1.0
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
      if diff>0
        option.changeSynapses 1
      elsif diff<0
        option.changeSynapses -1
      end
    end
  end
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
puts $synapses[0][0]
fitToDecision $decisions[0]
puts "-------------------------------------------------------------"
puts $synapses[0][0]
