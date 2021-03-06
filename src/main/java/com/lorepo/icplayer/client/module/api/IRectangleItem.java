package com.lorepo.icplayer.client.module.api;


public interface IRectangleItem{

	public ILayoutDefinition getLayout();
	public int	getLeft();
	public int	getRight();
	public int	getTop();
	public int	getBottom();
	public int	getWidth();
	public int	getHeight();
	
	public void setLeft(int left);
	public void setRight(int left);
	public void setTop(int top);
	public void setBottom(int top);
	public void setWidth(int width);
	public void setHeight(int height);
	
	public void disableChangeEvent(boolean disable);

}
