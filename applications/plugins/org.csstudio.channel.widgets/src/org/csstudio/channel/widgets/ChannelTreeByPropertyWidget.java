package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.utility.pvmanager.widgets.ErrorBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ChannelTreeByPropertyWidget extends AbstractChannelWidget {
	
	private Tree tree;
	private ErrorBar errorBar;
	
	private ChannelTreeByPropertyModel model;

	public Tree getTree() {
		return tree;
	}
	
	public ChannelTreeByPropertyWidget(Composite parent, int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		tree = new Tree(this, SWT.VIRTUAL | SWT.BORDER);
		tree.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				if (model != null) {
					TreeItem item = (TreeItem)event.item;
					TreeItem parentItem = item.getParentItem();
					ChannelTreeByPropertyNode parentNode;
					ChannelTreeByPropertyNode node;
					int index;
					if (parentItem == null) {
						parentNode = model.getRoot();
						index = tree.indexOf(item);
					} else {
						parentNode = (ChannelTreeByPropertyNode) parentItem.getData();
						index = parentItem.indexOf(item);
					}
					node = parentNode.getChild(index);
					item.setData(node);
					item.setText(node.getDisplayName());
					if (node.getChildrenNames() == null) {
						item.setItemCount(0);
					} else {
						item.setItemCount(node.getChildrenNames().size());
					}
				}
			}
		});
		tree.setItemCount(0);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectionWriter != null && tree.getSelectionCount() > 0) {
					selectionWriter.write(tree.getSelection()[0].getText());
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			List<String> properties = Arrays.asList("properties");
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					computeTree();
				}
			}
		});
	}
	
	private void setLastException(Exception ex) {
		errorBar.setException(ex);
	}
	
	private List<String> properties = new ArrayList<String>();
	private String selectionPv = null;
	private LocalUtilityPvManagerBridge selectionWriter = null;
	
	public List<String> getProperties() {
		return properties;
	}
	
	public void setProperties(List<String> properties) {
		List<String> oldProperties = this.properties;
		this.properties = properties;
		tree.setItemCount(0);
		tree.clearAll(true);
		changeSupport.firePropertyChange("properties", oldProperties, properties);
	}
	
	@Override
	protected void queryCleared() {
		tree.setItemCount(0);
		tree.clearAll(true);
	}
	
	@Override
	protected void queryExecuted(Result result) {
		if (result.exception == null) {
			List<String> newProperties = new ArrayList<String>(getProperties());
			newProperties.retainAll(ChannelUtil.getPropertyNames(result.channels));
			if (newProperties.size() != getProperties().size()) {
				setProperties(newProperties);
			}
			computeTree();
		} else {
			errorBar.setException(result.exception);
		}
	}
	
	private void computeTree() {
		tree.setItemCount(0);
		tree.clearAll(true);
		if (getChannelQuery() == null) {
			model = new ChannelTreeByPropertyModel(null, null, getProperties());
		} else if (getChannelQuery().getResult() == null) {
			model = new ChannelTreeByPropertyModel(getChannelQuery().getQuery(), null, getProperties());
		} else {
			model = new ChannelTreeByPropertyModel(getChannelQuery().getQuery(), getChannelQuery().getResult().channels, getProperties());
		}
		tree.setItemCount(model.getRoot().getChildrenNames().size());
	}
	
	public String getSelectionPv() {
		return selectionPv;
	}
	
	public void setSelectionPv(String selectionPv) {
		this.selectionPv = selectionPv;
		if (selectionPv == null || selectionPv.trim().isEmpty()) {
			// Close PVManager
			if (selectionWriter != null) {
				selectionWriter.close();
				selectionWriter = null;
			}
			
		} else {
			selectionWriter = new LocalUtilityPvManagerBridge(selectionPv);
		}
	}
}