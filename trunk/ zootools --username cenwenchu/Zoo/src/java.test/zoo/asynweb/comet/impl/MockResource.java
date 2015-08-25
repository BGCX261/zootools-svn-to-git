package zoo.asynweb.comet.impl;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zoo.asynweb.comet.IResource;

public class MockResource extends AbstractResource {

	private AtomicLong price = new AtomicLong(0);
	
	public long getPrice() {
		return price.get();
	}

	public void setPrice(long price) {
		this.price.set(price);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		if (request.getParameter("price") != null)
			this.price.set(Long.valueOf(request.getParameter("price")));
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) {
		if (request.getParameter("price") != null)
			this.price.set(Long.valueOf(request.getParameter("price")));
		
		if (request.getParameter("bid") != null)
			this.price.addAndGet(Long.valueOf(request.getParameter("bid")));
	}

	@Override
	public IResource doDelete(HttpServletRequest request,
			HttpServletResponse response) {
		return this;
	}

	@Override
	public String transformR2Str() {
		// TODO Auto-generated method stub
		return "price :" + price.get();
	}

	@Override
	public String getNotifyStr() {
		// TODO Auto-generated method stub
		return "price :" + price.get();
	}

}
