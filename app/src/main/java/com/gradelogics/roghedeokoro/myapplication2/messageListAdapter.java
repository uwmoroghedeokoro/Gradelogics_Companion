package com.gradelogics.roghedeokoro.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class messageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private ArrayList<msgObg> mMessageList;
    // We use this to hold the image state.
    private SparseBooleanArray mImageStates;


    public messageListAdapter(Context context, ArrayList<msgObg> messageList) {
        mContext = context;
        mMessageList = messageList;
        mImageStates = new SparseBooleanArray();
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        msgObg message = (msgObg) mMessageList.get(position);

        if (message.msgFrom.equals("Me")) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_msg_out, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_msg_in, parent, false);

           // view.setOnClickListener();
            return new ReceivedMessageHolder(view);
        }


        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        msgObg message = (msgObg) mMessageList.get(position);


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message,position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView image_att;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            image_att=(ImageView)itemView.findViewById(R.id.img_att);
           // image_att.setVisibility(View.GONE);

        }

        void bind(final msgObg message,int pos) {
            messageText.setText(message.msgBody);

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.msgDate);

            image_att.setVisibility(View.VISIBLE);


            if(!message.get_file_path().equals(""))
            {
               // Picasso.get().load(message.get_file_path()).into(image_att);
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addCategory(Intent.CATEGORY_BROWSABLE);
                        i.setData(Uri.parse(message.file_path));
                        i.setFlags(FLAG_ACTIVITY_NEW_TASK );

                        mContext.startActivity(i);
                    }
                });
                image_att.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addCategory(Intent.CATEGORY_BROWSABLE);
                        i.setData(Uri.parse(message.file_path));
                        i.setFlags(FLAG_ACTIVITY_NEW_TASK );

                        mContext.startActivity(i);
                    }
                });
                if (message.get_file_path().contains(".png") || message.get_file_path().contains(".jpg") || message.get_file_path().contains(".gif"))
                {
                    Picasso.get().load(message.get_file_path()).into(image_att);
                    messageText.setVisibility(View.GONE);

                }
                else if(message.get_file_path().contains(".pdf"))
                {
                    image_att.setImageResource(R.drawable.pdflogo);
                    image_att.getLayoutParams().width=80;
                }
            }else
            {
                image_att.setImageResource(0);
                messageText.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage,image_att;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            image_att=(ImageView)itemView.findViewById(R.id.img_att);
           // image_att.setVisibility(View.GONE);
        }

        void bind(final msgObg message) {
            messageText.setText(message.msgBody);
            if(!message.get_file_path().equals(""))
            {
                // Picasso.get().load(message.get_file_path()).into(image_att);
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addCategory(Intent.CATEGORY_BROWSABLE);
                        i.setData(Uri.parse(message.file_path));
                        i.setFlags(FLAG_ACTIVITY_NEW_TASK );

                        mContext.startActivity(i);
                    }
                });
                image_att.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addCategory(Intent.CATEGORY_BROWSABLE);
                        i.setData(Uri.parse(message.file_path));
                        i.setFlags(FLAG_ACTIVITY_NEW_TASK );

                        mContext.startActivity(i);
                    }
                });
                if (message.get_file_path().contains(".png") || message.get_file_path().contains(".jpg") || message.get_file_path().contains(".gif"))
                {
                    Picasso.get().load(message.get_file_path()).into(image_att);
                    messageText.setVisibility(View.GONE);

                }
                else if(message.get_file_path().contains(".pdf"))
                {
                    image_att.getLayoutParams().width=80;
                    image_att.setImageResource(R.drawable.pdflogo);
                }
            }else
            {
                image_att.setImageResource(0);
                messageText.setVisibility(View.VISIBLE);
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.msgDate);

            nameText.setText(message.msgFrom);
            if(message.msgType==1)
                nameText.setVisibility(View.GONE);
            else
                nameText.setVisibility(View.VISIBLE);
            // Insert the profile image from the URL into the ImageView.
           // Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

}
