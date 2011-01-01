/*
 * PsiTP4Flag
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

package psitp4.core;

/**
 *
 * @author Jaroslav Merxbauer
 */
public enum PsiTP4Flag {
    NONE,
    SYN,
    FIN,
    RST;

    public static PsiTP4Flag deserialize(byte raw) throws DeserializationException {
        raw &= 7; // mask out 5 leading values - they are not signficant
        switch (raw) {
            case 0:
                return NONE;
            case 1:
                return SYN;
            case 2:
                return FIN;
            case 4:
                return RST;
            default:
                throw new DeserializationException("Flags are mixed together! It is allowed to have only one flag set!");
        }
    }

    public static byte serialize(PsiTP4Flag flag) throws SerializationException {
        switch (flag) {
            case NONE:
                return 0;
            case SYN:
                return 1;
            case RST:
                return 2;
            case FIN:
                return 4;
            default:
                throw new SerializationException("Unusable flag used! Cannot serialize!");
        }
    }

}
