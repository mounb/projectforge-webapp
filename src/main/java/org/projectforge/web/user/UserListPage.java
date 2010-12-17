/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2010 Kai Reinhard (k.reinhard@me.com)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.web.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.projectforge.access.OperationType;
import org.projectforge.user.PFUserDO;
import org.projectforge.user.UserDao;
import org.projectforge.user.UserRightDO;
import org.projectforge.user.UserRightValue;
import org.projectforge.web.fibu.ISelectCallerPage;
import org.projectforge.web.wicket.AbstractListPage;
import org.projectforge.web.wicket.CellItemListener;
import org.projectforge.web.wicket.CellItemListenerPropertyColumn;
import org.projectforge.web.wicket.DetachableDOModel;
import org.projectforge.web.wicket.IListPageColumnsCreator;
import org.projectforge.web.wicket.ListPage;
import org.projectforge.web.wicket.ListSelectActionPanel;

@ListPage(editPage = UserEditPage.class)
public class UserListPage extends AbstractListPage<UserListForm, UserDao, PFUserDO> implements IListPageColumnsCreator<PFUserDO>
{
  private static final long serialVersionUID = 4408701323868106520L;

  @SpringBean(name = "userDao")
  private UserDao userDao;

  public UserListPage(final PageParameters parameters)
  {
    super(parameters, "user");
  }

  public UserListPage(final ISelectCallerPage caller, final String selectProperty)
  {
    super(caller, selectProperty, "user");
  }

  @SuppressWarnings("serial")
  @Override
  public List<IColumn<PFUserDO>> createColumns(final WebPage returnToPage)
  {
    final boolean updateAccess = userDao.hasAccess(null, null, OperationType.UPDATE, false);
    final List<IColumn<PFUserDO>> columns = new ArrayList<IColumn<PFUserDO>>();
    final CellItemListener<PFUserDO> cellItemListener = new CellItemListener<PFUserDO>() {
      public void populateItem(Item<ICellPopulator<PFUserDO>> item, String componentId, IModel<PFUserDO> rowModel)
      {
        final PFUserDO user = rowModel.getObject();
        final StringBuffer cssStyle = getCssStyle(user.getId(), user.isDeleted());
        if (cssStyle.length() > 0) {
          item.add(new AttributeModifier("style", true, new Model<String>(cssStyle.toString())));
        }
      }
    };
    columns.add(new CellItemListenerPropertyColumn<PFUserDO>(getString("user.username"), "username", "username", cellItemListener) {
      @SuppressWarnings("unchecked")
      @Override
      public void populateItem(final Item item, final String componentId, final IModel rowModel)
      {
        PFUserDO user = (PFUserDO) rowModel.getObject();
        if (isSelectMode() == true) {
          item.add(new ListSelectActionPanel(componentId, rowModel, caller, selectProperty, user.getId(), user.getUsername()));
          addRowClick(item);
        } else if (updateAccess == true) {
          item.add(new ListSelectActionPanel(componentId, rowModel, UserEditPage.class, user.getId(), returnToPage, user.getUsername()));
          addRowClick(item);
        } else {
          item.add(new Label(componentId, user.getUsername()));
        }
        cellItemListener.populateItem(item, componentId, rowModel);
      }
    });
    columns.add(new CellItemListenerPropertyColumn<PFUserDO>(getString("name"), "lastname", "lastname", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<PFUserDO>(getString("firstName"), "firstname", "firstname", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<PFUserDO>(getString("user.personalPhoneIdentifiers"), "personalPhoneIdentifiers",
        "personalPhoneIdentifiers", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<PFUserDO>(getString("description"), "description", "description", cellItemListener));
    if (updateAccess == true) {
      // Show these columns only for admin users:
      columns.add(new AbstractColumn<PFUserDO>(new Model<String>(getString("user.assignedGroups"))) {
        public void populateItem(Item<ICellPopulator<PFUserDO>> cellItem, String componentId, IModel<PFUserDO> rowModel)
        {
          PFUserDO user = rowModel.getObject();
          String groups = userDao.getGroupnames(user);
          Label label = new Label(componentId, new Model<String>(groups));
          cellItem.add(label);
          cellItemListener.populateItem(cellItem, componentId, rowModel);
        }
      });
      columns.add(new AbstractColumn<PFUserDO>(new Model<String>(getString("access.rights"))) {
        public void populateItem(Item<ICellPopulator<PFUserDO>> cellItem, String componentId, IModel<PFUserDO> rowModel)
        {
          final PFUserDO user = rowModel.getObject();
          final List<UserRightDO> rights = userDao.getUserRights(user.getId());
          final StringBuffer buf = new StringBuffer();
          if (rights != null) {
            boolean first = true;
            for (final UserRightDO right : rights) {
              if (right.getValue() != null && right.getValue() != UserRightValue.FALSE) {
                if (first == true) {
                  first = false;
                } else {
                  buf.append(", ");
                }
                buf.append(right.getRightId());
                if (right.getValue() == UserRightValue.READONLY) {
                  buf.append(" (ro)");
                } else if (right.getValue() == UserRightValue.PARTLYREADWRITE) {
                  buf.append(" (prw)");
                } else if (right.getValue() == UserRightValue.READWRITE) {
                  buf.append(" (rw)");
                }
              }
            }
          }
          Label label = new Label(componentId, buf.toString());
          cellItem.add(label);
          cellItemListener.populateItem(cellItem, componentId, rowModel);
        }
      });
    }
    return columns;
  }

  @Override
  protected void init()
  {
    dataTable = createDataTable(createColumns(this), "username", true);
    form.add(dataTable);
  }

  @Override
  protected UserListForm newListForm(AbstractListPage< ? , ? , ? > parentPage)
  {
    return new UserListForm(this);
  }

  @Override
  protected UserDao getBaseDao()
  {
    return userDao;
  }

  @Override
  protected IModel<PFUserDO> getModel(PFUserDO object)
  {
    return new DetachableDOModel<PFUserDO, UserDao>(object, getBaseDao());
  }

  protected UserDao getUserDao()
  {
    return userDao;
  }
}
