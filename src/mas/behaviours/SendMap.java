package mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import env.Attribute;
import mas.agents.ExploAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class SendMap extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2058134622078521998L;
	private AID receiverID;
	
	/**
	 * An agent tries to send a map to a friend
	 * @param myagent the agent who possesses the behaviur
	 *  
	 */
	public SendMap (final Agent myagent, AID receiverID) {
		super(myagent);
		this.receiverID = receiverID;
	}
	

	@Override
	public void action() {
		
		//get position to check if the agent is still alive
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=""){

			//create the suitable graph
			ArrayList<HashMap<String,List<Attribute>>> graph = null;
			graph = ((ExploAgent)this.myAgent).cropGraph(this.receiverID);

			//if there are nodes potentially unknown from the receiver
			if(!graph.isEmpty()){

				//display
				System.out.println("Agent "+this.myAgent.getLocalName()+ " is sending a map to Agent " + this.receiverID.getLocalName());

				ACLMessage msg = null;
				msg = new ACLMessage(ACLMessage.INFORM);
				msg.setSender(this.myAgent.getAID());
				msg.setLanguage("français");
				msg.addReceiver(receiverID);

				try {
					msg.setContentObject(graph);
				} catch (IOException e) {e.printStackTrace(); }

				((mas.abstractAgent)this.myAgent).sendMessage(msg);

				//listen for a confirmation through a behavior
				myAgent.addBehaviour(new ReceiveConfirmation(myAgent,receiverID));

			}
		}

	}

}

