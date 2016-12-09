package hendrawd.ganteng.movieinfo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * It is just a placeholder to make the swipe refresh works if the recycler view is empty
 *
 * @author hendrawd on 11/24/16
 */

public class EmptyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class Holder extends RecyclerView.ViewHolder {

        TextView textView;

        Holder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setText("No data!");
        return new Holder(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}