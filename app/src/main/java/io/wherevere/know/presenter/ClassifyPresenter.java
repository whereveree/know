package io.wherevere.know.presenter;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.wherevere.expandpopview.entity.KeyValue;
import io.wherevere.know.entity.ArticleData;
import io.wherevere.know.entity.ChildrenNode;
import io.wherevere.know.entity.Message;
import io.wherevere.know.entity.TreeNode;
import io.wherevere.know.fragment.ClassifyFragment;
import io.wherevere.know.model.ClassifyModel;
import io.wherevere.know.utils.GsonUtil;

/**
 * @author wherevere
 * @version 1.0
 * @time 2018/6/1 17:15
 */
public class ClassifyPresenter extends BasePresenter<ClassifyModel, ClassifyFragment> {

    public void getParentChildList() {
        model.getParentChildList(this);
    }

    public void setParentChildList(String body) {
        if (TextUtils.isEmpty(body)) {
            return;
        }
        Message msg = GsonUtil.gsonToBean(body, Message.class);
        if (msg == null) {

        } else {
            String json = GsonUtil.gsonToJson(msg.getData());
            List<TreeNode> treeNodes = GsonUtil.gsonToList(json, TreeNode.class);
            if (treeNodes != null) {
                List<KeyValue> childList = new ArrayList<>();
                List<KeyValue> parentList = new ArrayList<>();
                List<List<KeyValue>> parentChildrenList = new ArrayList<>();

                for (TreeNode t : treeNodes) {
                    String parentkey = t.getName();
                    String parentvalue = String.valueOf(t.getId());
                    KeyValue parentKeyValue = new KeyValue(parentkey, parentvalue);
                    parentList.add(parentKeyValue);

                    if (t.getChildren() != null) {
                        childList = new ArrayList<>();
                        for (ChildrenNode c : t.getChildren()) {
                            String key = c.getName();
                            String value = String.valueOf(c.getId());
                            KeyValue keyValue = new KeyValue(key, value);
                            childList.add(keyValue);
                        }
                        parentChildrenList.add(childList);

                    }
                }
                view.setExpandPopView(parentList, parentChildrenList);
            }
        }
    }

    public void getArticle(boolean is, int page, String value) {
        model.getArticle(is, page, value, this);
    }

    public void setArticle(boolean is, String body) {
        view.isLoading(false);
        if (TextUtils.isEmpty(body)) {
            return;
        }
        Message message = GsonUtil.gsonToBean(body, Message.class);
        if (view == null) {
            return;
        }
        if (message == null) {
            return;
        }
        if (0 == message.getErrorCode()) {
            String json = GsonUtil.gsonToJson(message.getData());
            ArticleData data = GsonUtil.gsonToBean(json, ArticleData.class);
            if (is) {
                view.refresh(data.getDatas());
            } else {
                view.loadmore(data.getDatas());
            }
        }
    }
}
