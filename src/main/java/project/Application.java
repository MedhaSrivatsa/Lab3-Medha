package project;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import CONFIG.SJSULabConfig;

//import com.oracle.webservices.internal.literal.ArrayList;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class Application {
	//VMInstance[] myvms;
	ServiceInstance si = null;
	public void pingvms() throws IOException
	{
		URL url = new URL(SJSULabConfig.getvCenterURL());
		si = new ServiceInstance(url, SJSULabConfig.getvCenterUsername(), SJSULabConfig.getPassword(), true);
		Folder rootFolder = si.getRootFolder();
		String rootname = rootFolder.getName();
		System.out.println("\n\n\tRoot name for vCenter: " + rootname + "\n\n");
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		if (mes == null || mes.length == 0) 
		{
			System.out.println("\n\tNo Virtual machine Found !!!");
			return;
		}
		java.util.ArrayList<VMHealthUpdateThread> list = new java.util.ArrayList<VMHealthUpdateThread>();
		
		for (int i = 0; i < mes.length; i++) {
			VirtualMachine vm = (VirtualMachine) mes[i];
			System.out.println("Collecting stats for vm : "+vm.getName());
			Runnable task = new VMHealthUpdateThread(vm.getName());
			Thread worker = new Thread(task);
			worker.start();
			//VMHealthUpdateThread vmobj = new VMHealthUpdateThread(vm.getName());
			//list.add(vmobj);
		}
		for (VMHealthUpdateThread vmHealthUpdateThread : list) {
			
			vmHealthUpdateThread.run();
		}
	}
	public static void main(String[] args) throws Exception {
		Application obj = new Application();
		obj.pingvms();
	}
}

