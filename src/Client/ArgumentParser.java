package Client;

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Server.Server;
import Server.ServerTCP;

public class ArgumentParser {
	private CommandLine commandLine;
	private String[] args;
	private String command;
	Resource tempResource;
	ArrayList<Server> ServerList;
	public ArgumentParser(String[] argument)
	{
		command = " ";
		args = argument;
		ServerList = new ArrayList<Server>();
		tempResource = new Resource(null,null,"test");
	}
	void parseInput(){
		Options options = new Options();

	    Option channel = new Option("channel", true, "channel");
	    channel.setRequired(false);
	    channel.setArgName("Channel name as String");
	    options.addOption(channel);

	    Option debug = new Option("debug", "print debug information");
	    debug.setRequired(false);
	    options.addOption(debug);

	    Option description = new Option("description", true,"resource description");
	    description.setRequired(false);
	    description.setArgName("DESCRIPTION AS STRING");
	    options.addOption(description);
	    
	    Option exchange = new Option("exchange",false,"exchange server list with server");
	    exchange.setRequired(false);
	    options.addOption(exchange);
	    
	    Option fetch = new Option("fetch", false,"fetch resources from server");
	    fetch.setRequired(false);
	    fetch.setArgName("URI AS STRING");
	    options.addOption(fetch);
	    
	    Option host = new Option("host", true,"server host, a domain name or IP address");
	    host.setRequired(false);
	    host.setArgName("SERVER NAME AS STRING");
	    options.addOption(host);
	    
	    Option name = new Option("name",true,"resource name");
	    name.setRequired(false);
	    name.setArgName("RESOURCE NAME AS STRING");
	    options.addOption(name);
	    
	    Option owner = new Option("owner", true,"owner");
	    owner.setRequired(false);
	    owner.setArgName("OWNER AS STRING");
	    options.addOption(owner);
	    
	    Option port = new Option("port", true ,"server port, an integer");
	    port.setRequired(false);
	    port.setArgName("INT");
	    options.addOption(port);
	    
	    Option publish = new Option("publish",false,"publish resource on server");
	    publish.setRequired(false);
	    options.addOption(publish);
	    
	    Option query = new Option("query", false,"query for resources on server");
	    query.setRequired(false);
	    options.addOption(query);
	    
	    Option remove = new Option("remove", false,"remove resource from server");
	    remove.setRequired(false);
	    options.addOption(remove);
	    
	    Option secret = new Option("secret", true, "secret");
	    secret.setRequired(false);
	    secret.setArgName("SECRET AS LONG STRING");
	    options.addOption(secret);
	    
	    Option servers = Option.builder("servers")
	    		.hasArgs()
	    		.valueSeparator(',')
	    		.build();
	    
	    Option share = new Option("share",false,"share resource on server");
	    share.setRequired(false);
	    options.addOption(share);
	    
	    Option tags = Option.builder("tags")
			.hasArgs()
			.valueSeparator(',')
			.build();
	    options.addOption(tags);
	    
	    Option uri = new Option("uri", true,"resource URI");
	    uri.setRequired(false);
	    options.addOption(uri);
	    	    
	    
	    CommandLineParser commandLineParser = new DefaultParser();
	    HelpFormatter helpFormatter = new HelpFormatter();

	    try {
	     commandLine = commandLineParser.parse(options, args);
	    } catch (ParseException e) {
	     System.out.println(e.getMessage());
	     helpFormatter.printHelp("utility-name", options);

	     System.exit(1);
	    }

	    String[] allCMD = {"publish","share","remove","query","fetch","exchange"};
	    
	    int count = 0;
	    for (int i = 0; i < 6; ++i){
		  if (commandLine.hasOption(allCMD[i])) {
			command = allCMD[i].toUpperCase();
			count++;
		  }
	    }
	    if (count != 1) {
	    command = " ";
	    }
	    updateLocalObjects();

	}
	private void updateLocalObjects()
	{
		switch(command)
		{
		case "PUBLISH":
		case "SHARE":
		case "REMOVE":
		case "QUERY":
		case "FETCH":
			updateTempResource();
			break;
		case "EXCHANGE":
			updateServerList();
			break;
		default:
			
			break;
		}
	}
	public JSONObject toJSON()
	{
		JSONObject tempJSONObject = new JSONObject();
		JSONArray tempJSONArray;
		tempJSONObject.put("command", command);
		switch(command)
		{
		case "PUBLISH":
		case "SHARE":
		case "REMOVE":
			JSONObject psr = tempResource.toJSON(); 
			tempJSONObject.put("resource", psr);
			
		case "QUERY":
		case "FETCH":
			JSONObject qf = tempResource.toJSON(); 
			tempJSONObject.put("resourceTemplate", qf);
			break;
		case "EXCHANGE":
			JSONArray ex = serverListToJSON();
			tempJSONObject.put("serverList", ex);
			break;
		default:
			
			break;
		}
		
		return tempJSONObject;
		
	}
	
	private void updateTempResource()
	{
		String name = commandLine.getOptionValue("name");
		String[] tags = commandLine.getOptionValues("tags");
		String description = commandLine.getOptionValue("description");
		String uri = commandLine.getOptionValue("uri");
		String channel = commandLine.getOptionValue("channel");
		String owner = commandLine.getOptionValue("owner");
		String host = commandLine.getOptionValue("host");
		int port = Integer.parseInt(commandLine.getOptionValue("port"));
		System.out.println("host:"+host+"port"+port);
		Server ezserver = new Server(host,port);
		tempResource = new Resource(name,tags,description,uri,
				channel, owner,ezserver);	
	}
	private void updateServerList()
	{
		String[] servers = commandLine.getOptionValues("servers");
		for(String s:servers)
		{
			if(s.contains(":"))
			{
			Server temp = new Server(s.split(":")[0] , Integer.parseInt(s.split(":")[1]));
			ServerList.add(temp);
			}
		}
		
	}
	private JSONArray serverListToJSON()
	{
		JSONArray tempArray = new JSONArray();
		for(Server s:ServerList)
		{
			JSONObject tempObject = null;
			if( s!= null )
			{
				tempObject = new JSONObject();
				tempObject.put("hostname", s.getHostname());
				tempObject.put("port", s.getPortString());
				tempArray.add(tempObject);
				System.out.print(tempObject.toString());
			}
		}
		return tempArray;	
	}

}
