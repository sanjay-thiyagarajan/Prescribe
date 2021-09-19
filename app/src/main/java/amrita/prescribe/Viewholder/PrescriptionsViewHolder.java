package amrita.prescribe.Viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import amrita.prescribe.R;

// ViewHolder for holding the prescription
public class PrescriptionsViewHolder extends RecyclerView.ViewHolder
{
    View mView;
    public PrescriptionsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    // Setter for Doctor's name
    public void setDr_name(String dr_name) {
        TextView tview = mView.findViewById(R.id.presc_view_dname);
        tview.setText(dr_name);
    }

    // Setter for prescription date
    public void setDate(String date)
    {
        TextView tview = mView.findViewById(R.id.presc_view_date);
        tview.setText(date);
    }
}
