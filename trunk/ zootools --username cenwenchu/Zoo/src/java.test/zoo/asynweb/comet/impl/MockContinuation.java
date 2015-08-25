/**
 * 
 */
package zoo.asynweb.comet.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationThrowable;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-10
 *
 */
public class MockContinuation implements Continuation {
	
	private ServletResponse servletResponse;

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#setTimeout(long)
	 */
	@Override
	public void setTimeout(long timeoutMs) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#suspend()
	 */
	@Override
	public void suspend() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#suspend(javax.servlet.ServletResponse)
	 */
	@Override
	public void suspend(ServletResponse response) {
		servletResponse = response;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#resume()
	 */
	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#complete()
	 */
	@Override
	public void complete() {
		try {
			this.servletResponse.getOutputStream().write("complete".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#isResumed()
	 */
	@Override
	public boolean isResumed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#isExpired()
	 */
	@Override
	public boolean isExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#isInitial()
	 */
	@Override
	public boolean isInitial() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#isResponseWrapped()
	 */
	@Override
	public boolean isResponseWrapped() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#getServletResponse()
	 */
	@Override
	public ServletResponse getServletResponse() {
		// TODO Auto-generated method stub
		return servletResponse;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#addContinuationListener(org.eclipse.jetty.continuation.ContinuationListener)
	 */
	@Override
	public void addContinuationListener(ContinuationListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object attribute) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.continuation.Continuation#undispatch()
	 */
	@Override
	public void undispatch() throws ContinuationThrowable {
		// TODO Auto-generated method stub

	}

}
