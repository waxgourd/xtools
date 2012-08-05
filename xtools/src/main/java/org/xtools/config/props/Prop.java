package org.xtools.config.props;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xtools.config.props.PropLine.LineType;

/**
 * simple prop <br/>
 * using <code>load</code> or <code>store</code> 
 * to read or write prop
 * 
 * @author waxgourd.x@gmail.com
 *
 */
public class Prop {
	private Map<String, Object> data = new HashMap<String, Object>();
	private List<PropPart> parts = new ArrayList<PropPart>();
	private PropConfig config = new PropConfig();
	
	private static String rootKey = "_root_";
	private static String valueKey = "_value_";
	
	public void add(String sectionName, 
			String key, String value, 
			String comment){
		if (key == null || "".equals(key.trim())
				|| value == null || "".equals(value.trim())) {
			throw new RuntimeException("key or value cannot be EMPTY !!! ");
		}
		
		PropPart part = null;
		if (sectionName == null || "".equals(sectionName)) {
			sectionName = rootKey;
		}else{
			sectionName = sectionName.trim();
		}
		if (sectionName.equals(rootKey)) {
			if(parts.size() > 0 && parts.get(0).getName().equals(rootKey)){
				part = parts.get(0);
			}else{
				part = new PropPart();
				part.setName(rootKey);
				parts.add(0, part);
			}
		}else{
			for (PropPart p : parts) {
				if (p.getName().equals(sectionName)) {
					part = p;
				}
			}
			if (part == null) {
				part = new PropPart();
				part.setName(sectionName);
				parts.add(part);
			}
		}
		
		PropLine line = new PropLine();
		line.setLineType(LineType.CommonLine);
		line.setKey(key.trim());
		line.setValue(value.trim());
		if (comment != null) {
			if (!comment.trim().startsWith("#")) {
				comment = "#"+comment;
			}
			line.setCommont(comment);
		}
		part.addLine(line);
		
		Map<String, Object> partMap = getPartMap(part.getName());
		loadLineData(partMap, line);
	}
	
	
	
	public void store(Writer writer) throws IOException{
		for (PropPart part : parts) {
			if(part.getName().equals(rootKey)){
				
			}else{
				writer.write("["+part.getName()+"]\n");
			}
			for (PropLine line : part.getLines()) {
				if (line.getLineType() == LineType.EmptyLine) {
					writer.write("\n");
				}else if (line.getLineType() == LineType.CommentLine) {
					writer.write(line.getCommont());
					writer.write("\n");
				}else if (line.getLineType() == LineType.CommonLine) {
					writer.write(line.getKey());
					char[] eq = new char[config.getSpacesBesidesEqual()*2+1];
					for (int i = 0; i < eq.length; i++) {
						if (i == config.getSpacesBesidesEqual()) {
							eq[i] = '=';
						}else{
							eq[i] = ' ';
						}
					}
					writer.write(eq);
					Object object;
					if(part.getName().equals(rootKey)){
						object = get(line.getKey());
					}else{
						object = get(part.getName()+"."+line.getKey());
					}
					writer.write(object.toString());
					if (line.getCommont() != null) {
						char[] cm = new char[config.getSpacesBeforeComment()];
						for (int i = 0; i < cm.length; i++) {
							cm[i] = ' ';
						}
						writer.write(cm);
						writer.write(line.getCommont());
					}
					writer.write("\n");
				}
			}
		}
		writer.flush();
	}
	
	public void load(BufferedReader reader) throws IOException{
		loadParts(reader);
		loadData();
	}
	
	public Object get(String key){
		if (key.contains(".")) {
			String[] keys = key.split("\\.");
			Map<String, Object> map = data;
			for (int i = 0; i < keys.length-1; i++) {
				map = (Map<String, Object>) map.get(keys[i]);
				if (map == null) {
					return null;
				}
			}
			Object object = map.get(keys[keys.length-1]);
			if (object == null) {
				return null;
			}
			if(object instanceof Map){
				Map<String, Object> m = (Map<String, Object>) object;
				return m.get(valueKey);
			}
			return object;
		}else{
			return data.get(key);
		}
	}
	
	private void loadParts(BufferedReader reader) throws IOException{
		PropPart rootPart = new PropPart();
		rootPart.setName(rootKey);
		
		PropPart lastPart = rootPart;
		String line = null;
		while((line = reader.readLine()) != null){
			if(line == null || line.trim().equals("")){
				PropLine propLine = new PropLine();
				propLine.setLineType(LineType.EmptyLine);
				lastPart.addLine(propLine);
				continue;
			}
			if (isCommentLine(line)) {
				PropLine propLine = new PropLine();
				propLine.setLineType(LineType.CommentLine);
				propLine.setValue(line.trim());
				propLine.setCommont(line.trim());
				lastPart.addLine(propLine);
				continue;
			}
			String sec = isSectionLine(line);
			if(sec == null || "".equals(sec)){
				PropLine commonLine = new PropLine();
				commonLine.setLineType(LineType.CommonLine);
				commonLine.setKey(line.substring(0, line.indexOf("=")).trim());
				String value = line.substring(line.indexOf("=")+1);//, line.indexOf("#"));
				String comment = "";
				if (value.indexOf("#") >= 0) {
					comment = value.substring(value.indexOf("#"));
					value = value.substring(0, value.indexOf("#"));
				}
				if (config.isValueTrim()) {
					value = value.trim();
				}
				commonLine.setValue(value);
				commonLine.setCommont(comment);
				lastPart.addLine(commonLine);
			}else{
				if (lastPart.getLines().size() > 0) {
					parts.add(lastPart);
				}
				PropPart part = new PropPart();
				part.setName(sec);
				lastPart = part;
			}
		}
		if (lastPart.getLines().size() > 0) {
			parts.add(lastPart);
		}
	}
	private static boolean isCommentLine(String line){
		if(line.trim().startsWith("#")){
			return true;
		}
		return false;
	}
	
	private static String isSectionLine(String line){
		Pattern pattern = Pattern.compile("\\s*\\[(.+)\\]\\s*");
		Matcher matcher = pattern.matcher(line);
		if(matcher.find()){
			String sectionName = matcher.group(1);
			return sectionName;
		}
		
		return null;
	}
	
	private void loadData(){
		for(PropPart part : parts){
			if (part.getLines().size() == 0) {
				continue;
			}
			Map<String, Object> partMap = getPartMap(part.getName());
			
			for (PropLine propLine : part.getLines()) {
				loadLineData(partMap, propLine);
			}
		}
	}



	private Map<String, Object> getPartMap(String sectionName) {
		Map<String, Object> partMap;
		if(sectionName.equals(rootKey)){
			partMap = data;
		} else {				
			Object object = data.get(sectionName);
			if(object == null){
				partMap = new HashMap<String, Object>();
			}else{
				partMap = (Map<String, Object>) object;					
			}
			data.put(sectionName, partMap);
		}
		return partMap;
	}



	private void loadLineData(Map<String, Object> partMap, PropLine propLine) {
		if (propLine.getLineType() == LineType.CommonLine) {
			String[] keys = propLine.getKey().split("\\.");
			if(keys.length == 1){
				partMap.put(keys[0], propLine.getValue());
			}else if(keys.length > 1){
				Map<String, Object> m = partMap;
				for (int i = 0; i < keys.length-1; i++) {
					Map<String, Object> m1;
					Object object = m.get(keys[i]);
					if(object == null){
						m1 = new HashMap<String, Object>();
					}else if(object instanceof Map){
						m1 = (Map<String, Object>) object;
					}else{
						m1 = new HashMap<String, Object>();
						m1.put(valueKey, object);
					}
					m.put(keys[i], m1);
					m = m1;
				}
				m.put(keys[keys.length-1], propLine.getValue());
			}
		}
	}
	
	
	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public PropConfig getConfig() {
		return config;
	}

	public void setConfig(PropConfig config) {
		this.config = config;
	}

	public List<PropPart> getParts() {
		return parts;
	}

	public void setParts(List<PropPart> parts) {
		this.parts = parts;
	}

	
}
