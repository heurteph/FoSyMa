package mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import env.Attribute;
import env.Environment;
import mas.abstractAgent;
import mas.behaviours.ExploreBehaviour;
import mas.behaviours.ReceiveHello;
import mas.behaviours.ReceiveMap;
import mas.behaviours.SendHello;
import mas.behaviours.SendMap;

public class ExploAgent extends abstractAgent {
	
	private static final long serialVersionUID = 4423634103678482649L;
	
	//memory of zones
	private Map<String,List<Attribute>> knownNodes    = new HashMap<String,List<Attribute>>();
	private Map<String,List<Attribute>> exploredNodes = new HashMap<String,List<Attribute>>();
	
	//memory of zone discovery
	private Map<String,Integer> knownNodesDate    = new HashMap<String,Integer>();
	private Map<String,Integer> exploredNodesDate = new HashMap<String,Integer>();
	
	//memory of meetings with other agents
	private Map<AID,Integer> agentLastMeetingDate = new HashMap<AID,Integer>();
	
	//intern clock
	private int clock;
	
	//explorer behavior
	private Behaviour explorer;
	
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){
			deployAgent((Environment) args[0]);
		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}

		//register the agent to the services
		register();
		
		//set the clock
		clock = 0;
		
		//Add the behaviors
		explorer = new ExploreBehaviour(this);
		addBehaviour(explorer);
		
		addBehaviour(new SendHello(this));
		addBehaviour(new ReceiveHello(this));

		System.out.println("the agent "+this.getLocalName()+ " is started");
	}
	
	
	//this method selects the suitable nodes to share with a specific agent
	public ArrayList<HashMap<String, List<Attribute>>> cropGraph(AID name){
		
		Integer lastTimeSeen = agentLastMeetingDate.get(name);
		if(lastTimeSeen == null)
			lastTimeSeen = 0;
		//crop known node graphs
		HashMap<String,List<Attribute>> knownNodesToShare = new HashMap<String,List<Attribute>>();
		for (Map.Entry<String, Integer> entry : knownNodesDate.entrySet())
		{
			//if a node has been discovered afterwards
			if(entry.getValue() > lastTimeSeen){
				knownNodesToShare.put(entry.getKey(), knownNodes.get(entry.getKey()));
			}
		}
		//crop explored node graphs
		HashMap<String,List<Attribute>> exploredNodesToShare = new HashMap<String,List<Attribute>>();
		for (Map.Entry<String, Integer> entry : exploredNodesDate.entrySet())
		{
			//if a node has been discovered afterwards
			if(entry.getValue() > lastTimeSeen){
				exploredNodesToShare.put(entry.getKey(), exploredNodes.get(entry.getKey()));
			}
		}
		//do a bundle
		ArrayList<HashMap<String, List<Attribute>>> bundle = new ArrayList<HashMap<String,List<Attribute>>>();
		bundle.add(knownNodesToShare);
		bundle.add(exploredNodesToShare);
		return bundle;
	}
	
	//this method merge the memory graph and the received graph
	public void mergeGraph(ArrayList<HashMap<String,List<Attribute>>> bundle){
		//if(receivedGraph.size() != 2){}
		HashMap<String,List<Attribute>> receivedKnownGraph    = bundle.get(0);
		HashMap<String,List<Attribute>> receivedExploredGraph = bundle.get(1);
		
		for (Map.Entry<String, List<Attribute>> entry : receivedKnownGraph.entrySet()){
			if(!knownNodes.containsKey(entry.getKey())){
				knownNodes.put(entry.getKey(),entry.getValue());
				knownNodesDate.put(entry.getKey(), clock);
			}
		}
		for (Map.Entry<String, List<Attribute>> entry : receivedExploredGraph.entrySet()){
			if(!exploredNodes.containsKey(entry.getKey())){
				exploredNodes.put(entry.getKey(),entry.getValue());
				exploredNodesDate.put(entry.getKey(), clock);
			}
		}
	}
	
	//this method to remember the last time it met an agent
	public void meetingWith(AID aid){
		agentLastMeetingDate.put(aid, clock);
	}
	
	//pause exploration for some time
	public void pauseExploration(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {e.printStackTrace();}
	}
	

	
	
//	protected void searchAgent(Agent agent,String type){
//		 DFAgentDescription df = new DFAgentDescription();
//		 ServiceDescription sd = new ServiceDescription();
//		 sd.setType(type);
//		 df.addServices(sd);
//		 DFAgentDescription[] result;
//		try {
//			 result = DFService.search(this, df);
//			 System.out.println(result.length + " results" );
//			 if (result.length>0)
//			 System.out.println(" " + result[0].getName() );
//		} catch (FIPAException e) {
//			 e.printStackTrace();
//		}
//	    }
	
	
	
	protected void deregister(){
		 try {
				DFService.deregister(this);
				System.out.println("Agent "+this.getLocalName()+" a été détruit!");
		} catch (FIPAException e) { e.printStackTrace(); }
	}
	
	private void register(){
		DFAgentDescription df = new DFAgentDescription();
		df.setName(this.getAID()); /* getAID est l'AID de l'agent qui veut s'enregistrer*/
		ServiceDescription sd = new ServiceDescription();
		sd.setType( "explorer" ); /* il faut donner des noms aux services qu'on propose (ici explorer)*/
		sd.setName(this.getLocalName());
		df.addServices(sd);
		try {
			DFService.register(this, df);
			System.out.println(this.getLocalName()+" a été enregistré");
		} catch (FIPAException fe) { fe.printStackTrace(); }
	}
	
	protected void takeDown(){
		//unregister the agent from the yellow pages
		deregister();
	}
	
	public Map<String,List<Attribute>> getKnownNodes(){
		return this.knownNodes;
	}
	
	public Map<String,List<Attribute>> getExploredNodes(){
		return this.exploredNodes;
	}


	public Map<String, Integer> getKnownNodesDate() {
		return knownNodesDate;
	}


	public Map<String, Integer> getExploredNodesDate() {
		return exploredNodesDate;
	}
	
	//this method sync the agent clock with his exploring behavior
	public void syncClock(int tick){
		this.clock = tick;
	}
}
