# ruby test.rb "desired_decisions_folder"

# globals declaration
$folder="civ3brains/"+ARGV[0]+"/"
$resources=23
$cityVarLength=($resources*2)+5
$plotVarLength=$resources+1
$maxLoyalty=5
$rand=Random.new
$decisions=[]
$synapses=[]

# class declaration
class Decision
  def initialize file
    File.open(file).each_with_index do |line,index|
      if index==0
        @correct=line.split ","
      else
        @data=eval(line)
      end
    end
  end
  def data
    return @data
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
    array=randSynapse
    array1=randSynapse
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

# initialization
Dir.entries($folder).each do |file|
  if File.extname(file)==".dec"
    $decisions.push Decision.new($folder+file)
  end
end
puts "All "+$decisions.length.to_s+" decisions loaded"
puts "Initiating brain generation..."
randomBrain
puts "Crude synapse network generated"
puts $synapses[0][0]
puts "Initiating network development..."
