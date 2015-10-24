package org.jslain.trains.train.manager.provider.handlers;

/**
 * No operations
 * 
 * @author ghislain
 *
 */
public class NoopHandler extends BaseHandler{

	@Override
	public boolean proceed() {
		return false;
	}

}
