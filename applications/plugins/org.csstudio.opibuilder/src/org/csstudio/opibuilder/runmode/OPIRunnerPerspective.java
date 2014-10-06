/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

/** Perspective for display runtime
 *  @author Xihui Chen - Original author
 *  @author Kay Kasemir
 */
public class OPIRunnerPerspective implements IPerspectiveFactory
{
    public enum Position
    {
        // Would like DEFAULT as, well, default, showing first in config UI etc.,
        // but config files store by option's index.
        // Changed order impacts existing display files.
        LEFT("Left", OPIView.ID + "LEFT"),
        RIGHT("Right", OPIView.ID + "RIGHT"),
        TOP("Top", OPIView.ID + "TOP"),
        BOTTOM("Bottom", OPIView.ID + "BOTTOM"),
        DETACHED("Detached", OPIView.ID),
        DEFAULT_VIEW("Default", OPIView.ID);
        
        private String description;
        private String view_id;
        
        private Position(final String description, final String view_id)
        {
             this.description = description;
             this.view_id = view_id;
        }

        /** ID of view to be used for displaying in this location */
        public String getOPIViewID()
        {
            return view_id;
        }
        
        public static String[] stringValues()
        {
            final String[] sv = new String[values().length];
            int i=0;
            for (Position p : values())
                sv[i++] = p.toString();
            return sv;
        }
        
        @Override
        public String toString()
        {
            return description;
        }
    }
    
    private static final String SECOND_ID = ":*"; //$NON-NLS-1$
    
    // Note that this used to be called "OPIRunner", just like the editor,
    // but in Eclipse 4 that re-used of the same ID for an Editor and a perspective caused trouble
    public final static String ID = "org.csstudio.opibuilder.OPIRuntime.perspective"; //$NON-NLS-1$
    
    private static final String ID_CONSOLE_VIEW =
        "org.eclipse.ui.console.ConsoleView";//$NON-NLS-1$
    
    @SuppressWarnings("deprecation")
    final static String ID_NAVIGATOR = IPageLayout.ID_RES_NAV;
    
    public void createInitialLayout(IPageLayout layout)
    {
        final String editor = layout.getEditorArea();
        
        // Hack using internal API:
        // Adds view stack in the editor area, so 'DEFAULT_VIEW' appears
        // similar to editor
        //
        // ModeledPageLayout real_layout = (ModeledPageLayout) layout;
        // real_layout.stackView(OPIView.ID + SECOND_ID, editor, false);
        
        // Create ordinary view stack for 'DEFAULT_VIEW' close to editor area
        final IPlaceholderFolderLayout center = layout.createFolder(Position.DEFAULT_VIEW.name(),
                IPageLayout.RIGHT, IPageLayout.RATIO_MAX, editor);
        center.addPlaceholder(OPIView.ID + SECOND_ID);
        
        final IPlaceholderFolderLayout left = layout.createFolder(Position.LEFT.name(),
                IPageLayout.LEFT, 0.25f, editor);
        left.addPlaceholder(Position.LEFT.getOPIViewID() + SECOND_ID);
        
        final IPlaceholderFolderLayout right = layout.createFolder(Position.RIGHT.name(),
                IPageLayout.RIGHT, 0.75f, editor);
        right.addPlaceholder(Position.RIGHT.getOPIViewID() + SECOND_ID);
        
        final IPlaceholderFolderLayout top = layout.createFolder(Position.TOP.name(),
                IPageLayout.TOP, 0.25f, editor);
        top.addPlaceholder(Position.TOP.getOPIViewID() + SECOND_ID);
        
        final IPlaceholderFolderLayout bottom = layout.createFolder(Position.BOTTOM.name(),
                IPageLayout.BOTTOM, 0.75f, editor);
        bottom.addPlaceholder(Position.BOTTOM.getOPIViewID() + SECOND_ID);
        
        if (!OPIBuilderPlugin.isRAP())
        {
            bottom.addPlaceholder(ID_CONSOLE_VIEW);
            layout.addShowViewShortcut(ID_CONSOLE_VIEW);
            left.addPlaceholder(ID_NAVIGATOR);
            layout.addShowViewShortcut(ID_NAVIGATOR);
        }
    }
}
