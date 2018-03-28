package vakoze.blomidtech.vakoze.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.models.Commentaire;

public class CommentItemAdapter extends BaseAdapter {

    private List<Commentaire> objects;

    private Context context;
    private LayoutInflater layoutInflater;

    public CommentItemAdapter(Context context, List<Commentaire> commentaire) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = commentaire;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Commentaire getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.comment_item, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((Commentaire)getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(Commentaire object, ViewHolder holder) {
        //TODO implement
    }

    protected class ViewHolder {
        private TextView dateAjout;
    private TextView profileName;
    private TextView commentaire;
    private ImageView profilePic;

        public ViewHolder(View view) {
            dateAjout = (TextView) view.findViewById(R.id.dateAjout);
            profileName = (TextView) view.findViewById(R.id.profileName);
            commentaire = (TextView) view.findViewById(R.id.commentaire);
            profilePic = (ImageView) view.findViewById(R.id.profilePic);
        }
    }
}
