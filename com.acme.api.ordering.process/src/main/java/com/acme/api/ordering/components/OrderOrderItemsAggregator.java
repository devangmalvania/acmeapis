package com.acme.api.ordering.components;



import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.routing.AggregationContext;
import org.mule.routing.AggregationStrategy;


public class OrderOrderItemsAggregator implements AggregationStrategy {
	
	@Override
	public MuleEvent aggregate(AggregationContext context) throws MuleException {
		String strOrder ;
		String strOrderItems;
		
		MuleEvent finalEvent = DefaultMuleEvent.copy(context.getOriginalEvent());
		// Loop over all messages without exceptions
		for( MuleEvent e : context.collectEventsWithExceptions())
		{
			if( e.getMessage().getExceptionPayload().getMessage().contains("404"))
			{
				throw new org.mule.module.apikit.exception.NotFoundException("Order ID does not exist!"); 
			}
			
		}
		MuleEvent orderEvent = context.collectEventsWithoutExceptions().get(0);
		MuleEvent orderItemEvent = context.collectEventsWithoutExceptions().get(1);
				
		if ( orderEvent.getMessage().getPayload() == null || orderItemEvent.getMessage().getPayload() == null)
		{
			throw new org.mule.module.apikit.exception.NotFoundException("Order ID does not exist!"); 
		}
		strOrder = orderEvent.getMessage().getPayload().toString();
		
		strOrderItems = orderItemEvent.getMessage().getPayload().toString();
		strOrderItems =  strOrderItems.trim().substring(1, strOrderItems.length()-2);
		strOrder = strOrder.replace("\"orderItems\": 123", strOrderItems);
		
		
		MuleMessage finalMessage = new DefaultMuleMessage(strOrder, context.getOriginalEvent().getMuleContext()); 
			finalEvent.setMessage(finalMessage); 
			return finalEvent;
		
	}
}