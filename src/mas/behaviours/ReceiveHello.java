package mas.behaviours;

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

public class ReceiveHello extends TickerBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who possesses the behavior
	 *  
	 */
	public ReceiveHello (final Agent myagent) {
		super(myagent, 10000);
	}


	@Override
	public void onTick() {
		//get position to check if the agent is still alive
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=""){

			//is waiting for a proposal
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
			ACLMessage msg = myAgent.receive(mt);
			
			if(msg != null){
				//accept proposal
				ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				reply.setSender(this.myAgent.getAID());
				reply.setLanguage("français");
				reply.addReceiver(msg.getSender());
				((mas.abstractAgent)this.myAgent).sendMessage(msg);
				
				//display
				System.out.println("Agent "+this.myAgent.getLocalName()+ " has accepted the proposition from Agent " + msg.getSender().getLocalName());
				
				//wait for the map
				myAgent.addBehaviour(new ReceiveMap(myAgent, msg.getSender()));
			}
			else{
				//stop agent and do timeout
				int millis = 2000;
				((ExploAgent)this.myAgent).pauseExploration(millis);
				block(millis);
			}
		}
		
	}

}