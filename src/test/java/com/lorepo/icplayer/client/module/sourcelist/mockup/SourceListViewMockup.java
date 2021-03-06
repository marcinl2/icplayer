package com.lorepo.icplayer.client.module.sourcelist.mockup;

import java.util.HashMap;

import com.google.gwt.dom.client.Element;
import com.lorepo.icplayer.client.module.sourcelist.IViewListener;
import com.lorepo.icplayer.client.module.sourcelist.SourceListModule;
import com.lorepo.icplayer.client.module.sourcelist.SourceListPresenter.IDisplay;

public class SourceListViewMockup implements IDisplay {

	private IViewListener listener;
	private String selectedId;
	private HashMap<String, String> items = new HashMap<String, String>();
	
	
	public SourceListViewMockup(SourceListModule model) {
	}

	@Override
	public void selectItem(String id) {
		selectedId = id;
	}

	@Override
	public void addListener(IViewListener l) {
		this.listener = l;
	}

	public void click(String id){
		listener.onItemCliked(id);
	}
	
	@Override
	public void addItem(String id, String value, boolean callMathJax) {
		
		if(items.get(id) != null){
			System.out.println("Duplicate entry");
			id = id + System.currentTimeMillis(); 
		}
		
		items.put(id, value);
	}

	@Override
	public void removeItem(String id) {
		items.remove(id);
	}

	@Override
	public void removeAll() {
		items.clear();
	}

	@Override
	public void deselectItem(String id) {
		if(selectedId.compareTo(id) == 0){
			selectedId = null;
		}
	}
	
	
	public String getSelectedId(){
		return selectedId;
	}
	
	public HashMap<String, String> getItems(){
		return items;
	}

	@Override
	public Element getElement() {
		// TODO Auto-generated method stub
		return null;
	}

}
