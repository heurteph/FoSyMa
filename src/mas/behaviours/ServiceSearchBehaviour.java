package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ServiceSearchBehaviour extends TickerBehaviour {
	
	public String type;
		
	 public ServiceSearchBehaviour(final mas.abstractAgent myagent, int temps,String type){
		 super(myagent,temps);
		 this.type = type;
 
	 }
	
	 public void searchAgent(Agent agent){
		 DFAgentDescription df = new DFAgentDescription();
		 ServiceDescription sd = new ServiceDescription();
		 sd.setType(this.type);
		 df.addServices(sd);
		 DFAgentDescription[] result;
		try {
			result = DFService.search(this.myAgent, df);
			 System.out.println(result.length + " results" );
			 if (result.length>0)
			 System.out.println(" " + result[0].getName() );
		} catch (FIPAException e) {
			 e.printStackTrace();
		}
	    }
	 


	@Override
	protected void onTick() {
		searchAgent(this.myAgent);
	}

}
