package org.getspout.spoutapi.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public interface Widget{
	
	public int getNumBytes();

	public int getVersion();
	
	public WidgetType getType();
	
	/**
	 * Returns a unique id for this widget
	 * @return id
	 */
	public UUID getId();
	
	public void render();
	
	public void readData(DataInputStream input) throws IOException;
	
	public void writeData(DataOutputStream output) throws IOException;
	
	/**
	 * Marks this widget as needing an update on the client. It will be updated after the next onTick call, and marked as setDirty(false)
	 * Every widget is dirty immediately after creation
	 * @param dirty
	 */
	public void setDirty(boolean dirty);
	
	/**
	 * Is true if this widget has been marked dirty
	 * @return dirty
	 */
	public boolean isDirty();
	
	/**
	 * Gets the render priority for this widget. Highest priorities render first (in the background), the lowest priorities render on top (in the foreground).
	 * @return priority.
	 */
	public RenderPriority getPriority();
	
	/**
	 * Sets the render priority for this widget. Highest priorities render first (in the background), the lowest priorities render on top (in the foreground).
	 * @param priority to render at
	 * @return widget
	 */
	public Widget setPriority(RenderPriority priority);
	
	/**
	 * Gets the width of this widget, in pixels
	 * @return width 
	 */
	public int getWidth();
	
	/**
	 * Sets the width of this widget, in pixels
	 * @param width to set
	 * @return widget
	 */
	public Widget setWidth(int width);
	
	/**
	 * Gets the height of this widget, in pixels
	 * @return height
	 */
	public int getHeight();
	
	/**
	 * Sets the height of this widget, in pixels
	 * @param height to set
	 * @return widget
	 */
	public Widget setHeight(int height);
	
	/**
	 * Gets the screen this widget is attached to, or null if unattached
	 * @return screen
	 */
	public Screen getScreen();
	
	/**
	 * Sets the screen this widget is attached to. Should not be used normally, is handled with screen.attachWidget() is called.
	 * @param screen this is attached to
	 * @return widget
	 */
	public Widget setScreen(Screen screen);

	/**
	 * Sets the screen and plugin this widget is attached to. Should not be used normally, is handled with screen.attachWidget() is called.
	 * @param screen this is attached to
	 * @param plugin this is attached to
	 * @return widget
	 */
	public Widget setScreen(Plugin plugin, Screen screen);

	/**
	 * Gets the x coordinate of this widget. Widgets (and screens) render from the top left cornor the screen. 0,0 represents the top left corner.
	 * @return x-coordinate
	 */
	public int getX();
	
	/**
	 * Gets the y coordinate of this widget. Widgets (and screens) render from the top left cornor the screen. 0,0 represents the top left corner.
	 * @return y-coordinate
	 */
	public int getY();
	
	/**
	 * Sets the x coordinate of this widget. Widgets (and screens) render from the top left cornor the screen. 0,0 represents the top left corner.
	 * @param pos to set
	 * @return widget
	 */
	public Widget setX(int pos);
	
	/**
	 *  Sets the y coordinate of this widget. Widgets (and screens) render from the top left cornor the screen. 0,0 represents the top left corner.
	 * @param pos to set
	 * @return widget
	 */
	public Widget setY(int pos);
	
	/**
	 * Shifts this widget the given number of pixels in the x direction.
	 * @param x pixels to shift
	 * @return widget
	 */
	public Widget shiftXPos(int x);
	
	/**
	 * Shifts this widget the given number of pixels in the y direction
	 * @param y pixels to shift
	 * @return widget
	 */
	public Widget shiftYPos(int y);
	
	/**
	 * Is true if this widget is visible and rendering on the screen
	 * @return visible
	 */
	public boolean isVisible();
	
	/**
	 * Sets the visibility of this widget. If true, it will render normally. If false, it will not appear on the screen.
	 * @param enable the visibility
	 * @return widget
	 */
	public Widget setVisible(boolean enable);
	
	/**
	 * Called each tick this widget is updated. This widget is processed for isDirty() immediately afterwords.
	 */
	public void onTick();
	
	/**
	 * Set the widget's tooltip.
	 * Returns the current instance of the widget to make chainable calls.
	 */
	public Widget setTooltip(String tooltip);
	
	/**
	 * Gets the widget's tooltip
	 */
	public String getTooltip();
	
	/**
	 * Gets the widget's container
	 */
	public Container getContainer();
	
	/**
	 * Sets the parant container for this widget
	 */
	public void setContainer(Container container);
	
	/**
	 * Container Layout - Set whether the widget will be resized with it's container
	 * @param fixed if it is a static size
	 * @return the container
	 */
	public Widget setFixed(boolean fixed);
	
	/**
	 * Use isFixed() instead
	 * @deprecated
	 */
	public boolean getFixed();

	/**
	 * Container Layout - Whether the widget is fixed size inside it's container
	 * @return 
	 */
	public boolean isFixed();

	// NOTE: Margins follow the same order as CSS
	/**
	 * Container Layout - Padding to use for automatic container layout - not included in dimensions
	 * @param margin sets the same for all sides
	 * @return 
	 */
	public Widget setMargin(int marginAll);

	/**
	 * Container Layout - Padding to use for automatic container layout - not included in dimensions
	 * @param marginTopBottom
	 * @param marginLeftRight
	 * @return 
	 */
	public Widget setMargin(int marginTopBottom, int marginLeftRight);

	/**
	 * Container Layout - Padding to use for automatic container layout - not included in dimensions
	 * @param marginTop
	 * @param marginLeftRight
	 * @param marginBottom
	 * @return 
	 */
	public Widget setMargin(int marginTop, int marginLeftRight, int marginBottom);

	/**
	 * Container Layout - Padding to use for automatic container layout - not included in dimensions
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 * @param marginLeft
	 * @return 
	 */
	public Widget setMargin(int marginTop, int marginRight, int marginBottom, int marginLeft);

	/**
	 * Container Layout - Get the margin used for container layout
	 * @return 
	 */
	public int getMarginTop();

	/**
	 * Container Layout - Get the margin used for container layout
	 * @return 
	 */
	public int getMarginRight();

	/**
	 * Container Layout - Get the margin used for container layout
	 * @return 
	 */
	public int getMarginBottom();

	/**
	 * Container Layout - Get the margin used for container layout
	 * @return 
	 */
	public int getMarginLeft();

	/**
	 * Set the anchor point for this widget, default is CENTER
	 * @param anchor 
	 * @return
	 */
	public Widget setAnchor(WidgetAnchor anchor);

	/**
	 * Get the current anchor position
	 * @return 
	 */
	public WidgetAnchor getAnchor();
}
