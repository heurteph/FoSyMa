//package mas.agents;
//
//
//import jade.domain.DFService;
//import jade.domain.FIPAException;
//import env.Environment;
//import mas.abstractAgent;
//import mas.behaviours.*;
//
//
//
//public class DummyExploAgent extends ServiceAgent{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -1784844593772918359L;
//	
//
//
//
//	/**
//	 * This method is automatically called when "agent".start() is executed.
//	 * Consider that Agent is launched for the first time. 
//	 * 			1) set the agent attributes 
//	 *	 		2) add the behaviours
//	 *          
//	 */
//	protected void setup(){
//
//		super.setup();
//
//       
//		//Add the behaviours
//		addBehaviour(new RandomWalkBehaviour(this));
//		addBehaviour(new SayHello(this));
//	
//
//		
//
//	}
//
//	/**
//	 * This method is automatically called after doDelete()
//	 */
//	protected void takeDown(){
//			deregister();
//	}
//}
