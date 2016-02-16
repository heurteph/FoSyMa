package mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mas.agents.ExploAgent;
import env.Attribute;
import env.Environment.Couple;
import jade.core.behaviours.TickerBehaviour;

public class ExploreBehaviour extends TickerBehaviour{
	
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	
	public ExploreBehaviour (final ExploAgent myagent) {
		super(myagent, 1000);
	}
	
	//add an explored node to the list, if it's not already there
	public void addToExploreredNodes(Map<String,List<Attribute>> exploredNodes,
									 Map<String,Integer> exploredNodesDate,
									 String myPosition,
									 List<Attribute> lattribute)
	{	
		if (!exploredNodes.containsKey(myPosition)){
			exploredNodes.put(myPosition, lattribute);
			//record the date of the discovery
			exploredNodesDate.put(myPosition, this.getTickCount());
		}
	}
	
	
	//add a known node to the list, if it's not already there
	public void addToKnownNodes(Map<String,List<Attribute>> knownNodes,
								Map<String,Integer> knownNodesDate,
								String position,
								List<Attribute> lattribute)
	{
		if (!knownNodes.containsKey(position)){
				knownNodes.put(position, lattribute);
				//record the date of the discovery
				knownNodesDate.put(position, this.getTickCount());
		}
	}

	@Override
	public void onTick() {
		//retrieve the current position to check if the agent is still alive
		String myPosition=((ExploAgent)this.myAgent).getCurrentPosition();
		
		
		if (myPosition!=""){
			
			//synchronization
			((ExploAgent)this.myAgent).syncClock(getTickCount());
			
			//list of observable from the agent's current position
			List<Couple<String,List<Attribute>>> lobs = ((ExploAgent)this.myAgent).observe();//myPosition

			System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			//System.out.println("current position: "+myPosition);
			
			//list of attribute associated to the currentPosition
			List<Attribute> lattribute= lobs.get(0).getRight();
			
			
			//list of nodes from agent's memory
			Map<String,List<Attribute>> exploredNodes = ((ExploAgent)this.myAgent).getExploredNodes();
			Map<String,List<Attribute>> knownNodes    = ((ExploAgent)this.myAgent).getKnownNodes();
			
			Map<String,Integer> exploredNodesDate = ((ExploAgent)this.myAgent).getExploredNodesDate();
			Map<String,Integer> knownNodesDate    = ((ExploAgent)this.myAgent).getKnownNodesDate();
			
			
			//update of the agent's memory from observations
			addToExploreredNodes(exploredNodes, exploredNodesDate, myPosition, lattribute);
			for (int i=0; i<lobs.size(); i++){
				addToKnownNodes(knownNodes, knownNodesDate, lobs.get(i).getLeft(), lobs.get(i).getRight());
			}
			System.out.println(this.myAgent.getLocalName()+" -- list of explored nodes in memory: "+ exploredNodes.keySet());
			System.out.println(this.myAgent.getLocalName()+" -- list of known nodes in memory: "+ knownNodes.keySet());
			
			
			//termination criteria :
			//"when every known node has been explored"
			if(knownNodes.size() == exploredNodes.size()){
				System.out.println(this.myAgent.getLocalName()+" -- The " + knownNodes.size() + " nodes of the graph have been explored");
				this.stop();
			}
			
			
			//example related to the use of the backpack for the treasure hunt
//			Boolean b=false;
//			for(Attribute a:lattribute){
//				switch (a) {
//				case TREASURE:
//					System.out.println("My current backpack capacity is:"+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
//					System.out.println("Value of the treasure on the current position: "+a.getValue());
//					System.out.println("The agent grabbed :"+((mas.abstractAgent)this.myAgent).pick());
//					System.out.println("the remaining backpack capacity is: "+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
//					System.out.println("The value of treasure on the current position: (unchanged before a new call to observe()): "+a.getValue());
//					b=true;
//					break;
//
//				default:
//					break;
//				}
//			}

			//If the agent picked (part of) the treasure
//			if (b){
//				List<Couple<String,List<Attribute>>> lobs2=((mas.abstractAgent)this.myAgent).observe();//myPosition
//				System.out.println("lobs after picking "+lobs2);
//			}


			//list all unexplored neighbor from the current position
			List<Couple<String,List<Attribute>>> unexploredNodes = new ArrayList<Couple<String,List<Attribute>>>();
			for(Couple<String,List<Attribute>> neighbour : lobs){
				if(!exploredNodes.containsKey(neighbour.getLeft())){
					unexploredNodes.add(neighbour);
				}
			}
			
			List<Couple<String,List<Attribute>>> choice;
			if(!unexploredNodes.isEmpty()){
				choice = unexploredNodes;
			}
			//if no unexplored nodes around, do a random walk
			else{
				choice = lobs;
				choice.remove(0); //but don't stay on the spot
			}
			
			Random r= new Random();
			int moveId=r.nextInt(choice.size());
			//The move action (if any) should be the last action of your behavior
			((mas.abstractAgent)this.myAgent).moveTo(choice.get(moveId).getLeft());
		}

	}

}
