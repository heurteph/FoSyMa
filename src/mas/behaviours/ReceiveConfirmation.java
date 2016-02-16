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

public class ReceiveConfirmation extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	
	private AID receiverID;
	
	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who possesses the behavior
	 *  
	 */
	public ReceiveConfirmation (final Agent myagent, AID receiverID) {
		super(myagent);
		this.receiverID = receiverID;
	}


	@Override
	public void action() {
		//get the current position to check if the agent is still alive
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=""){
			//is waiting for a confirmation
			MessageTemplate mt = MessageTemplate.and(
								 MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
								 MessageTemplate.MatchSender(receiverID));
			ACLMessage msg = myAgent.receive(mt);
			
			if(msg != null){
				//remember the meeting
				((ExploAgent)this.myAgent).meetingWith(this.receiverID);
				
				//display
				System.out.println("Agent "+this.myAgent.getLocalName()+ " has received a confirmation from Agent " + this.receiverID.getLocalName());
			
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