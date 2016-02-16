package mas.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mas.agents.ExploAgent;
import env.Attribute;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMap extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private AID receiverID;
	
	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who posses the behaviour
	 *  
	 */
	public ReceiveMap (final Agent myagent, AID receiverID) {
		super(myagent);
		this.receiverID = receiverID;
	}
	

	@Override
	public void action() {
		//get the current position to check if the agent is still alive
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=""){
			
			MessageTemplate mt = MessageTemplate.and(
								 MessageTemplate.MatchPerformative(ACLMessage.INFORM),
								 MessageTemplate.MatchSender(this.receiverID));
			ACLMessage msg = myAgent.receive(mt); //agent stops until timeout

			if(msg != null){
				//update the memory from the message
				ArrayList<HashMap<String,List<Attribute>>> receivedGraph = null;
				try {
					receivedGraph = (ArrayList<HashMap<String, List<Attribute>>>) msg.getContentObject();
				} catch (UnreadableException e) {e.printStackTrace(); }
				((ExploAgent)myAgent).mergeGraph(receivedGraph);

				//display
				System.out.println("Agent "+this.myAgent.getLocalName()+ " has updated his map from Agent " + this.receiverID.getLocalName());

				//confirm its update to the sender
				ACLMessage reply;
				reply = new ACLMessage(ACLMessage.CONFIRM);
				reply.setSender(this.myAgent.getAID());
				reply.setLanguage("français");
				reply.addReceiver(this.receiverID);
				((mas.abstractAgent)this.myAgent).sendMessage(reply);
			}
			else{
				//stop exploring but continue exchange, unless timeout occurs
				int millis = 2000;
				((ExploAgent)this.myAgent).pauseExploration(millis);
				block(millis);
			}
		}
	}

}