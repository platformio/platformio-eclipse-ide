package com.insightmachines.pio.activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class PioActivator extends AbstractUIPlugin {
	
	private static PioActivator plugin;
	
	public static PioActivator getDefault() {
		return plugin;
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		getLog().log( new Status(IStatus.INFO, getBundle().getSymbolicName(), "Finishing Platform IO installation...") );
		// This is just a stub for the actual PIO installation job 
		Job installJob = new Job("Finishing Platform IO installation...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Sit back, relax and watch us work for a little while ..", IProgressMonitor.UNKNOWN);
				try {
					// Run the actual installation here
					Thread.sleep(10000);
					monitor.setTaskName("Done!");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		installJob.setPriority(Job.LONG);
		installJob.setUser(true);
		installJob.schedule();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		super.stop(context);
	}

}
