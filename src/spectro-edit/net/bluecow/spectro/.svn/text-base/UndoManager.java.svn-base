/*
 * Created on Aug 21, 2008
 *
 * Spectro-Edit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectro-Edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package net.bluecow.spectro;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoManager extends javax.swing.undo.UndoManager {

    private static final Logger logger = Logger.getLogger(UndoManager.class.getName());
    
    public UndoManager() {
        super();
        setLimit(1000);
    }
    
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        logger.finest("Got undoable edit: " + e.getEdit());
        super.undoableEditHappened(e);
        logger.fine("Added edit " + edits.size() + "/" + getLimit());
    }
    
    @Override
    protected void undoTo(UndoableEdit edit) throws CannotUndoException {
        super.undoTo(edit);
        fireStateChanged();
    }
    
    @Override
    protected void redoTo(UndoableEdit edit) throws CannotRedoException {
        super.redoTo(edit);
        fireStateChanged();
    }
    
    @Override
    public synchronized boolean addEdit(UndoableEdit anEdit) {
        boolean added = super.addEdit(anEdit);
        fireStateChanged();
        return added;
    }
    
    private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    public void addChangeListener(ChangeListener l) {
        changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }

    public void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this); // TODO fill in constructor args
        for (int i = changeListeners.size() - 1; i >= 0; i--) {
            changeListeners.get(i).stateChanged(e);
        }
    }
}
