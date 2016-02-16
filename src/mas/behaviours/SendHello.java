package mas.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



//import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import mas.agents.ExploAgent;
//import mas.agents.ExploAgent;
import env.Attribute;

public class SendHello extends TickerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4118520821055443159L;


	/**
	 * An agent tries to contact its friend
	 * @param myagent the agent who possesses the behavior
	 *  
	 */
	public SendHello (final Agent myagent) {
		super(myagent, 10000);
	}
	

	@Override
	public void onTick() {
		
		//get position to check if the agent is still alive
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		
		ACLMessage msg = null;
		
		if (myPosition!=""){
			
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("explorer");
			dfd.addServices(sd);
			DFAgentDescription[] result = null;
			try {
				result = DFService.search(this.myAgent, dfd);
			} catch (FIPAException e) { e.printStackTrace(); }
			
			if(result.length > 0){
				
				for(DFAgentDescription receiver : result){

					AID receiverID = receiver.getName();
					//do not send the message to itself
					if(!receiverID.equals(this.myAgent.getAID())){
						
						
						msg = new ACLMessage(ACLMessage.PROPOSE);
						msg.setSender(this.myAgent.getAID());
						msg.setLanguage("français");
						msg.addReceiver(receiverID);
						((mas.abstractAgent)this.myAgent).sendMessage(msg);
						
						//display
						System.out.println("Agent "+this.myAgent.getLocalName()+ " is making a proposition to Agent " + receiverID.getLocalName());
						
						//is waiting for the answer
						MessageTemplate mt = MessageTemplate.and(
											 MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
											 MessageTemplate.MatchSender(receiverID));
						ACLMessage answer = myAgent.receive(mt); //agent stops until timeout
						
						if(answer != null){
							//send the map
							myAgent.addBehaviour(new SendMap(myAgent, receiverID));
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

		}

	}

}