/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2013 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// task community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// task community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with task program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.web.rest.converter;

import org.projectforge.core.DefaultBaseDO;
import org.projectforge.rest.AbstractBaseObject;

/**
 * For conversion of DefaultBaseDO to base object.
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
public class DOConverter
{
  public static void copyFields(final AbstractBaseObject dest, final DefaultBaseDO src)
  {
    dest.setId(src.getId());
    dest.setDeleted(src.isDeleted());
    dest.setCreated(src.getCreated());
    dest.setLastUpdate(src.getLastUpdate());
  }
}