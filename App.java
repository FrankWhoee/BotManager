package bot.central.BotManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class App extends ListenerAdapter
{
	public static boolean shellLoud = false;
	
	public static JDA jda;
    public static void main( String[] args ) throws Exception
    {
        jda = new JDABuilder(AccountType.BOT).setToken(Ref.TOKEN).buildBlocking();
        jda.addEventListener(new App());
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
    	//Objects
    	User objUser = evt.getAuthor();
    	MessageChannel objMsgCh= evt.getChannel();
    	Message objMsg = evt.getMessage();
    	Guild objGuild = evt.getGuild();
    	
    	if(objMsg.getContentRaw().startsWith(Ref.prefix) && !Ref.adminIds.contains(objUser.getIdLong())){
    		objMsgCh.sendMessage("YOU ARE NOT AUTHORISED TO USE BOTMANAGER.");
    		return;
    	}
    	
    	if(objMsg.getContentRaw().startsWith(Ref.prefix + "update")) {
    		
    		//Verifications
    		if(objMsg.getAttachments().size() == 0) {
    			objMsgCh.sendMessage("[ERROR]: No file attached!").queue();
    			return;
    		}
    		
    		String input = objMsg.getContentRaw();
    		String bot = input.substring(8).trim();
    		String link = objMsg.getAttachments().get(0).getUrl();
    		
    		if(!objMsg.getAttachments().get(0).getFileName().equalsIgnoreCase(bot + ".zip")) {
    			objMsgCh.sendMessage("[ERROR]: ZIP filename does not match bot name.").queue();
    			return;
    		}
    	
    		
    		if(bot.equalsIgnoreCase("bismuth")) {
    			objMsgCh.sendMessage("$init 0").queue(message -> {
    				String output = "";
    				if(sh("ls").contains("Bismuth.zip")) {
    					output += sh("rm Bismuth.zip");
    				}
    				output += sh("rm -r Bismuth");
    				output += sh("wget " + link);
    				output += sh("unzip Bismuth.zip");
    				output += sh("rm Bismuth.zip");
    				if(!sh("ls Bismuth").contains("run.sh")) {
    					output += sh("wget "+Ref.BismuthRunScript+" -P Bismuth");
        				output += sh("chmod +x Bismuth/run.sh");
    				}
    				objMsgCh.sendMessage("Bismuth has been updated. Outputting results:").queue();
    				try{
    					objMsgCh.sendMessage(output).queue();
    				}catch(Exception e) {
    					objMsgCh.sendMessage("`Bismuth was succesfully updated.`").queue();
    				}
    				objMsgCh.sendMessage("Starting up Bismuth...").queue();
    				exec("cd Bismuth && ./run.sh",true);
    				
    				
    				
    				
    			});
    		}else if(bot.equalsIgnoreCase("htbackup")) {
    			objMsgCh.sendMessage(">init 0").queue(message -> {
    				String output = "";
    				
    				if(sh("ls").contains("HT-Backup.zip")) {
    					output += sh("rm HT-Backup.zip");
    				}
    				output += sh("rm -r HT-Backup");
    				output += sh("wget " + link);
    				output += sh("unzip HT-Backup.zip");
    				output += sh("rm HT-Backup.zip");
    				if(!sh("ls HT-Backup").contains("run.sh")) {
    					output += sh("wget "+Ref.HTBackupRunScript+" -P HTBackup");
        				output += sh("chmod +x HTBackup/run.sh");
    				}
    				
    				objMsgCh.sendMessage("HT Backup has been updated.").queue();
    				objMsgCh.sendMessage("Starting up HT-Backup...").queue();
    				exec("cd HT-Backup && ./run.sh",true);  
    				try {
    					objMsgCh.sendMessage(output).queue();
    				}catch(Exception e) {
    					objMsgCh.sendMessage("`HT Backup was succesfully updated.`");
    				}
    			});
    		}else if(bot.equalsIgnoreCase("BotManager")) {
    			String output = "";
    			output += sh("rm -r BotManager");
				output += sh("wget " + link);
				output += sh("unzip BotManager.zip");
				output += sh("rm BotManager.zip");
				output += sh("wget "+Ref.BotManagerRunScript+" -P BotManager");
				output += sh("chmod +x BotManager/run.sh");
				
				objMsgCh.sendMessage("You must manually reboot BotManager for updates to take effect.").queue();
				objMsgCh.sendMessage("Remember to use `mvn clean` and `mvn package` to destroy and old files.").queue();
				objMsgCh.sendMessage("To shut me down, use ~terminate").queue();
				objMsgCh.sendMessage("Here is what was outputted: \n" + output.substring(0, Math.min(2000, output.length()))).queue();
    		}else if(bot.equalsIgnoreCase("vegas")) {
    			objMsgCh.sendMessage("Vegas can not be updated in " + Ref.version).queue();
    		}else if(bot.equalsIgnoreCase("testcase")) {
    			objMsgCh.sendMessage("Test case succeeded.").queue();
    		}
    		
    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "start")) {
    		
    		String input = objMsg.getContentRaw();
    		String bot = input.substring(7).trim();
    		objMsgCh.sendMessage("Starting up " + bot.substring(0, 1).toUpperCase() + bot.substring(1) + "...").queue();
    		if(bot.equalsIgnoreCase("bismuth")) {
    			exec("cd Bismuth && ./run.sh",true);
    		}else if(bot.equalsIgnoreCase("htbackup")) {
    			exec("cd HT-Backup && ./run.sh",true);
    		}
    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "terminate")) {
    		jda.shutdownNow();
    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "sh")) {
    		String input = objMsg.getContentRaw();
    		String cmd = input.substring(4).trim();
    		if(!shellLoud) {
    			objMsgCh.sendMessage("Command disabled. To enable, type "+Ref.prefix+"toggleSh").queue();
    		}else {
    			objMsgCh.sendMessage(sh(cmd)).queue();
    			shellLoud = false;
    			objMsgCh.sendMessage("shellLoud has been set to false").queue();
    		}
    		
    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "toggleSh")) {
    		if(shellLoud) {
    			objMsgCh.sendMessage("shellLoud set to false.").queue();
    			shellLoud = false;
    		}else {
    			objMsgCh.sendMessage("shellLoud set to true. WARNING: Running commands that don't stop will freeze BotManager.").queue();
    			shellLoud = true;
    		}
    		
    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "exec")) {
    		String input = objMsg.getContentRaw();
    		String cmd = input.substring(6,input.indexOf("/")).trim();
    		String dir = input.substring(input.indexOf("/"));
    		
    		exec(cmd);
    		objMsgCh.sendMessage("Command sent.");
    	}else if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "status")) {
    		objMsgCh.sendMessage(objUser.getAsMention() + " `" + Ref.version + ": ONLINE`").queue();
    	}else if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "help")) {
    		objMsgCh.sendMessage("```BotManager Manual:"
    				+ "\n~update [BOTNAME] <FILE.ZIP>: Updates bot. Use HTBackup instead of HT-Backup for updating HT Backup."
    				+ "\n~start [BOTNAME]: Starts bot."
    				+ "\n~terminate: Shuts down BotManager."
    				+ "\n~exec [COMMAND]: Runs COMMAND in shell."
    				+ "\n~sh [COMMAND]: Returns output from command.```").queue();
    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix) && objMsg.getContentRaw().length() > 0) {
    		objMsgCh.sendMessage("Invalid command.").queue();
    	}
    	
    }
    
    public static String sh(String command) {
    	//Build command 
    	Process process = null;
		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", command})
			    .redirectErrorStream(true)
			    .directory(new File("../"))
			    .start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //Read output
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null, previous = null;
        
        String output = "";
        try {
			while ((line = br.readLine()) != null ) {
			    if (!line.equals(previous)) {
			        previous = line;
			        out.append(line).append('\n');
			        output += line + "\n";
			    }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return output;
    }
    
    public static void exec(String command,boolean doNotWait) {
    	System.out.println(command);
    	//Build command 
    	Process process = null;
		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", command})
			    .redirectErrorStream(true)
			    .directory(new File("../"))
			    .start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		if(!doNotWait) {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//process.destroy();
    }
    
    public static void exec(String command) {
    	System.out.println(command);
    	//Build command 
    	Process process = null;
		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", command})
			    .redirectErrorStream(true)
			    .directory(new File("../"))
			    .start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//process.destroy();
    }
    
    public static String cloudExec(String command) {
    	//Build command 
    	Process process = null;
		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", command})
			    .redirectErrorStream(true)
			    .directory(new File("../"))
			    .start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //Read output
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null, previous = null;
        
        String output = "";
        try {
			while ((line = br.readLine()) != null)
			    if (!line.equals(previous)) {
			        previous = line;
			        out.append(line).append('\n');
			        output += line + "\n";
			    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return output;
    }
}
