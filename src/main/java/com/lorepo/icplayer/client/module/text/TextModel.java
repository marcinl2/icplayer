package com.lorepo.icplayer.client.module.text;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.lorepo.icf.properties.IBooleanProperty;
import com.lorepo.icf.properties.IEnumSetProperty;
import com.lorepo.icf.properties.IHtmlProperty;
import com.lorepo.icf.properties.IProperty;
import com.lorepo.icf.utils.StringUtils;
import com.lorepo.icf.utils.UUID;
import com.lorepo.icf.utils.XMLUtils;
import com.lorepo.icf.utils.i18n.DictionaryWrapper;
import com.lorepo.icplayer.client.module.BasicModuleModel;
import com.lorepo.icplayer.client.module.text.TextParser.ParserResult;


/**
 * Prostokątny obszar o podanym kolorze i rodzaju ramki
 * 
 * @author Krzysztof Langner
 *
 */
public class TextModel extends BasicModuleModel{

	public String parsedText;
	public List<GapInfo>	gapInfos = new ArrayList<GapInfo>();
	public List<InlineChoiceInfo>	choiceInfos = new ArrayList<InlineChoiceInfo>();
	public List<LinkInfo>	linkInfos = new ArrayList<LinkInfo>();

	private String moduleText = "";
	private boolean useDraggableGaps;
	private boolean openLinksinNewTab = true;
	private int gapWidth = 0;
	private boolean isActivity = true;
	private boolean isDisabled = false;
	private boolean isCaseSensitive = false;
	private boolean isIgnorePunctuation = false;
	public String rawText;
	public String gapUniqueId = "";
	
	
	public TextModel() {
		super(DictionaryWrapper.get("text_module"));
		gapUniqueId = UUID.uuid(6);
		setText(DictionaryWrapper.get("text_module_default"));
		addPropertyDraggable();
		addPropertyGapWidth();
		addPropertyIsActivity();
		addPropertyIsDisabled();
		addPropertyIsCaseSensitive();
		addPropertyIsIgnorePunctuation();
		addPropertyOpenLinksinNewTab();
		addPropertyText();
		
	}
	
	
	@Override
	public void setId(String id){
		super.setId(id);
		if(rawText != null){
			setText(rawText);
		}
	}
	
	
	public String getGapUniqueId(){
		return gapUniqueId;
	}
	
	
	public String getParsedText(){
		return parsedText;
	}
	
	
	public boolean hasDraggableGaps(){
		return useDraggableGaps;
	}
	
	
	public int getGapWidth(){
		return gapWidth;
	}
	
	
	@Override
	public void load(Element node, String baseUrl) {

		super.load(node, baseUrl);
		
		NodeList nodes = node.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			
			Node childNode = nodes.item(i);
			if(childNode instanceof Element){
				
				if(childNode.getNodeName().compareTo("text") == 0){

					Element textElement = (Element) childNode;
					useDraggableGaps = XMLUtils.getAttributeAsBoolean(textElement, "draggable");
					gapWidth = XMLUtils.getAttributeAsInt(textElement, "gapWidth");
					isActivity = XMLUtils.getAttributeAsBoolean(textElement, "isActivity", true);
					isDisabled = XMLUtils.getAttributeAsBoolean(textElement, "isDisabled", false);
					isCaseSensitive = XMLUtils.getAttributeAsBoolean(textElement, "isCaseSensitive", false);
					isIgnorePunctuation = XMLUtils.getAttributeAsBoolean(textElement, "isIgnorePunctuation", false);
					openLinksinNewTab = XMLUtils.getAttributeAsBoolean(textElement, "openLinksinNewTab", true);
					rawText = XMLUtils.getCharacterDataFromElement(textElement);
					if(rawText == null){
						rawText = XMLUtils.getText(textElement);
						rawText = StringUtils.unescapeXML(rawText);
					}
					setText(rawText);
					
				}
			}
		}
	}

	
	private void setText(String text) {

		moduleText = text;
		TextParser parser = new TextParser();
		parser.setId(gapUniqueId);
		parser.setUseDraggableGaps(useDraggableGaps);
		parser.setCaseSensitiveGaps(isCaseSensitive);
		parser.setIgnorePunctuationGaps(isIgnorePunctuation);
		parser.setOpenLinksinNewTab(openLinksinNewTab);
		ParserResult parsedTextInfo = parser.parse(moduleText);
		parsedText = parsedTextInfo.parsedText;
		gapInfos = parsedTextInfo.gapInfos;
		choiceInfos = parsedTextInfo.choiceInfos;
		linkInfos = parsedTextInfo.linkInfos;
		if(getBaseURL() != null){
			parsedText = StringUtils.updateLinks(parsedText, getBaseURL());
		}
	}


	@Override
	public String toXML() {
		
		String xml = "<textModule " + getBaseXML() + ">" + getLayoutXML();
		xml += "<text draggable='" + useDraggableGaps + "' " +
				"gapWidth='" + gapWidth + "' isActivity='" + isActivity + "' " +
				"isIgnorePunctuation='" + isIgnorePunctuation + 
				"' isDisabled='" + isDisabled + "' isCaseSensitive='" + isCaseSensitive + 
				"' openLinksinNewTab='" + openLinksinNewTab + 
				"'><![CDATA[" + moduleText + "]]></text>";
		xml += "</textModule>";
		
		return XMLUtils.removeIllegalCharacters(xml);
	}


	private void addPropertyText() {

		IHtmlProperty property = new IHtmlProperty() {
				
			@Override
			public void setValue(String newValue) {
				setText(newValue);
				sendPropertyChangedEvent(this);
			}
			
			@Override
			public String getValue() {
				return moduleText;
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("text_module_text");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("text_module_text");
			}
		};
		
		addProperty(property);
	}
	
	
	private void addPropertyDraggable() {

		IProperty property = new IEnumSetProperty() {
			
			@Override
			public void setValue(String newValue) {
				useDraggableGaps = newValue.compareTo("Draggable") == 0;
				setText(moduleText);
				sendPropertyChangedEvent(this);
			}
			
			@Override
			public String getValue() {
				if(useDraggableGaps){
					return "Draggable";
				}
				else{
					return "Editable";
				}
			}

			@Override
			public String getName() {
				return DictionaryWrapper.get("text_module_gap_type");
			}

			@Override
			public int getAllowedValueCount() {
				return 2;
			}

			@Override
			public String getAllowedValue(int index) {
				if(index == 0){
					return "Editable";
				}
				else{
					return "Draggable";
				}
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("text_module_gap_type");
			}
		};
		
		addProperty(property);
	}
	
	private void addPropertyOpenLinksinNewTab() {

		IProperty property = new IEnumSetProperty() {
			
			@Override
			public void setValue(String newValue) {
				openLinksinNewTab = newValue.compareTo("New Tab") == 0;
				setText(moduleText);
				sendPropertyChangedEvent(this);
			}
			
			@Override
			public String getValue() {
				if(openLinksinNewTab){
					return "New Tab";
				}
				else{
					return "Same Tab";
				}
			}

			@Override
			public String getName() {
				return DictionaryWrapper.get("open_links_in_new_tab");
			}

			@Override
			public int getAllowedValueCount() {
				return 2;
			}

			@Override
			public String getAllowedValue(int index) {
				if(index == 0){
					return "New Tab";
				}
				else{
					return "Same Tab";
				}
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("open_links_in_new_tab");
			}
		};
		
		addProperty(property);
	}
	

	private void addPropertyGapWidth() {

		IProperty property = new IProperty() {
			
			@Override
			public void setValue(String newValue) {
				gapWidth = Integer.parseInt(newValue);
				sendPropertyChangedEvent(this);
			}
			
			@Override
			public String getValue() {
				return Integer.toString(gapWidth);
			}

			@Override
			public String getName() {
				return DictionaryWrapper.get("text_module_gap_width");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("text_module_gap_width");
			}
		};
		
		addProperty(property);
	}
	

	public List<GapInfo> getGapInfos() {
		return gapInfos;
	}


	public List<InlineChoiceInfo> getChoiceInfos() {
		return choiceInfos;
	}


	public List<LinkInfo> getLinkInfos() {
		return linkInfos;
	}


	public boolean isActivity() {
		return isActivity;
	}
	
	
	private void addPropertyIsActivity() {
		
		IProperty property = new IBooleanProperty() {
			
			@Override
			public void setValue(String newValue) {
				boolean value = (newValue.compareToIgnoreCase("true") == 0); 
				
				if(value!= isActivity){
					isActivity = value;
					sendPropertyChangedEvent(this);
				}
			}
			
			@Override
			public String getValue() {
				if(isActivity){
					return "True";
				}
				else{
					return "False";
				}
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("is_activity");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("is_activity");
			}

		};
		
		addProperty(property);	
	}


	private void addPropertyIsDisabled() {
		
		IProperty property = new IBooleanProperty() {
			
			@Override
			public void setValue(String newValue) {
				boolean value = (newValue.compareToIgnoreCase("true") == 0); 
				
				if(value!= isDisabled){
					isDisabled = value;
					sendPropertyChangedEvent(this);
				}
			}
			
			@Override
			public String getValue() {
				if(isDisabled){
					return "True";
				}
				else{
					return "False";
				}
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("is_disabled");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("is_disabled");
			}

		};
		
		addProperty(property);	
	}


	private void addPropertyIsCaseSensitive() {
		
		IProperty property = new IBooleanProperty() {
			
			@Override
			public void setValue(String newValue) {
				boolean value = (newValue.compareToIgnoreCase("true") == 0); 
				
				if(value!= isCaseSensitive){
					isCaseSensitive = value;
					sendPropertyChangedEvent(this);
				}
			}
			
			@Override
			public String getValue() {
				if(isCaseSensitive){
					return "True";
				}
				else{
					return "False";
				}
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("case_sensitive");
			}

			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("case_sensitive");
			}

		};
		
		addProperty(property);	
	}


	private void addPropertyIsIgnorePunctuation() {
		
		IProperty property = new IBooleanProperty() {
			
			@Override
			public void setValue(String newValue) {
				boolean value = (newValue.compareToIgnoreCase("true") == 0); 
				
				if(value!= isIgnorePunctuation){
					isIgnorePunctuation = value;
					sendPropertyChangedEvent(this);
				}
			}
			
			@Override
			public String getValue() {
				if(isIgnorePunctuation){
					return "True";
				}
				else{
					return "False";
				}
			}
			
			@Override
			public String getName() {
				return DictionaryWrapper.get("Ignore_punctuation");
			}
			
			@Override
			public String getDisplayName() {
				return DictionaryWrapper.get("Ignore_punctuation");
			}


		};
		
		addProperty(property);	
	}


	public boolean isDisabled() {
		return isDisabled;
	}

	
	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}


	public boolean isIgnorePunctuation() {
		return isIgnorePunctuation;
	}
	
	public boolean openLinksinNewTab() {
		return openLinksinNewTab;
	}

}
