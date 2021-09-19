package amrita.prescribe.Viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import amrita.prescribe.R;

// ViewHolder for holding the Reports
public class ReportsViewHolder extends RecyclerView.ViewHolder
{
    View mView;
    public ReportsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    // Setter for title
    public void setTitle(String title) {
        TextView textView = mView.findViewById(R.id.rep_view_title);
        textView.setText(title);
    }

    //Setter for date
    public void setDate(String date) {
        TextView textView = mView.findViewById(R.id.rep_view_date);
        textView.setText(date);
    }

}
