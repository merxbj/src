/*
 * ProgressSink
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package psitp4.application;

import psitp4.core.PsiTP4Connection;
import psitp4.core.PsiTP4Packet;

/**
 *
 * @author Jaroslav Merxbauer
 * @authoer %I% %G%
 */
public interface ProgressSink {

    public void onWindowSlide(long bytes);
    public void onDataGramReceived(PsiTP4Packet packet);
    public void onDataGramSent(PsiTP4Packet packet);
    public void onConnectionOpen(PsiTP4Connection con);
    public void onConnectionClose(PsiTP4Connection con);

}
