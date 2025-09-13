package com.example.myemailapp.adapters;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myemailapp.R;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.utils.EmailActionHandler;
import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private List<Email> emails;
    private OnEmailClickListener listener;
    private boolean isTrashContext;
    private EmailActionHandler actionHandler;
    private OnEmailListUpdateListener listUpdateListener;



    public interface OnEmailListUpdateListener {
        void onEmailRemoved(String emailId);
        void onEmailReadStatusChanged(String emailId, boolean isRead);
        void onEmailStarStatusChanged(String emailId, boolean isStarred);
        void onEmailLabelsChanged(String emailId, List<String> labels);
    }

    public interface OnEmailClickListener {
        void onEmailClick(Email email);
        void onRestoreClick(Email email);
        void onDeleteClick(Email email);
        void onMarkReadClick(Email email);
        void onMarkUnreadClick(Email email);
        void onSpamClick(Email email);
        void onLabelsClick(Email email);
    }

    public EmailAdapter(List<Email> emails, OnEmailClickListener listener, boolean isTrashContext,
                        EmailActionHandler actionHandler, OnEmailListUpdateListener listUpdateListener) {
        this.emails = emails;
        this.listener = listener;
        this.isTrashContext = isTrashContext;
        this.actionHandler = actionHandler;
        this.listUpdateListener = listUpdateListener;
    }

    public void updateEmailList(List<Email> newEmails) {
        this.emails = newEmails;
        notifyDataSetChanged();
    }

    public int getPositionById(String id) {
        for (int i = 0; i < emails.size(); i++)
            if (emails.get(i).getId().equals(id)) return i;
        return -1;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new EmailViewHolder(view, isTrashContext, actionHandler, listUpdateListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
//        holder.bind(emails.get(position), listener);
        Email email = emails.get(position);
        holder.bind(email, listener);
        if (email.isRead()) {
            holder.itemContainer.setSelected(false); // Read → white
        } else {
            holder.itemContainer.setSelected(true);  // Unread → orange-ish
        }

    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    static class EmailViewHolder extends RecyclerView.ViewHolder {
        private TextView textFrom;
        private TextView textSubjectBody;
        private TextView textLabels;
        private TextView textTimestamp;
        private ImageButton buttonRestore;
        private ImageButton buttonDelete;
        private ImageButton buttonMarkRead;
        private ImageButton buttonMarkUnread;
        private ImageButton buttonSpam;
        private ImageButton buttonLabels;
        private ImageButton buttonStar;
        private View itemContainer;
        private final boolean isTrashContext;
        private final EmailActionHandler actionHandler;
        private final OnEmailListUpdateListener listUpdateListener;

        public EmailViewHolder(@NonNull View itemView, boolean isTrashContext,
                               EmailActionHandler actionHandler, OnEmailListUpdateListener listUpdateListener) {
            super(itemView);
            this.isTrashContext = isTrashContext;
            this.actionHandler = actionHandler;
            this.listUpdateListener = listUpdateListener;

            textFrom = itemView.findViewById(R.id.text_from);
            textSubjectBody = itemView.findViewById(R.id.text_subject_body);
            textLabels = itemView.findViewById(R.id.text_labels);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            buttonRestore = itemView.findViewById(R.id.button_restore);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonMarkRead = itemView.findViewById(R.id.button_mark_read);
            buttonMarkUnread = itemView.findViewById(R.id.button_mark_unread);
            buttonSpam = itemView.findViewById(R.id.button_spam);
            buttonLabels = itemView.findViewById(R.id.button_labels);
            buttonStar = itemView.findViewById(R.id.button_star);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(Email email, OnEmailClickListener listener) {
            Context ctx = itemView.getContext();

            textFrom.setText(email.getFrom());

            // Combine subject and body for the third line
            String subject = email.getSubject() != null ? email.getSubject() : "(No Subject)";
            String body = email.getBody() != null ? email.getBody() : "";
            String subjectBody = subject + " - " + body;

            // Handle draft indicator with red color for "Draft" prefix
            //if ("drafts".equals(email.getTrashSource()) || email.isDraftInSpam()) {
            if (false) {
                String draftPrefix = ctx.getString(R.string.draft_prefix, "");
                String fullText = draftPrefix + subject + " - " + body;
                SpannableString spannable = new SpannableString(fullText);
                spannable.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(ctx, android.R.color.holo_red_dark)),
                        0, draftPrefix.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                textSubjectBody.setText(spannable);
            } else {
                textSubjectBody.setText(subjectBody);
            }

            // Handle labels with rounded background
//            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
//                String labelsText = String.join("  ", email.getLabels()); // Extra space for padding
//                SpannableString spannableLabels = new SpannableString(labelsText);
//                int start = 0;
//                for (String label : email.getLabels()) {
//                    int end = start + label.length();
//                    spannableLabels.setSpan(
//                            new RoundedBackgroundSpan(
//                                    ContextCompat.getColor(ctx, R.color.yellow),
//                                    ContextCompat.getColor(ctx, android.R.color.black),
//                                    12f, // Increased corner radius for larger appearance
//                                    6f,  // Increased horizontal padding
//                                    6f   // Increased vertical padding
//                            ),
//                            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                    );
//                    start = end + 2; // Account for the double space separator
//                }
//                textLabels.setText(spannableLabels);
//                textLabels.setVisibility(View.VISIBLE);
//            } else {
//                textLabels.setText(ctx.getString(R.string.no_labels));
//                textLabels.setVisibility(View.VISIBLE);
//            }
            if (email.getLabels() != null && !email.getLabels().isEmpty()) {
                SpannableString spannableLabels;
                if (email.getLabels().size() == 1) {
                    String label = email.getLabels().get(0);
                    // Add padding spaces to mimic multiple-label behavior
                    String paddedLabel = label + "  "; // Add two spaces to match String.join separator
                    spannableLabels = new SpannableString(paddedLabel);
                    spannableLabels.setSpan(
                            new RoundedBackgroundSpan(
                                    ContextCompat.getColor(ctx, R.color.yellow),
                                    ContextCompat.getColor(ctx, android.R.color.black),
                                    12f, 8f, 6f
                            ),
                            0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                } else {
                    String labelsText = String.join("  ", email.getLabels());
                    spannableLabels = new SpannableString(labelsText);
                    int start = 0;
                    for (String label : email.getLabels()) {
                        int end = start + label.length();
                        spannableLabels.setSpan(
                                new RoundedBackgroundSpan(
                                        ContextCompat.getColor(ctx, R.color.yellow),
                                        ContextCompat.getColor(ctx, android.R.color.black),
                                        12f, 8f, 6f
                                ),
                                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        start = end + 2;
                    }
                }
                textLabels.setText(spannableLabels);
                textLabels.setVisibility(View.VISIBLE);
            } else {
                textLabels.setText(ctx.getString(R.string.no_labels));
                textLabels.setVisibility(View.VISIBLE);
            }

            textTimestamp.setText(email.getTimeStamp());

            itemContainer.setSelected(! email.isRead());
            buttonMarkRead.setVisibility(email.isRead()   ? View.GONE : View.VISIBLE);
            buttonMarkUnread.setVisibility(email.isRead() ? View.VISIBLE : View.GONE);

            // Handle star button
            if (email.isStarred()) {
                buttonStar.setImageResource(R.drawable.star_filled);
            } else {
                buttonStar.setImageResource(R.drawable.star_outline);
            }

            // Handle trash context for spam button
            if (isTrashContext) {
                buttonStar.setVisibility(View.GONE);
                buttonRestore.setVisibility(View.VISIBLE);
            } else {
                buttonStar.setVisibility(View.VISIBLE);
                buttonRestore.setVisibility(View.GONE);
            }
            // show spam button everywhere
            buttonSpam.setVisibility(View.VISIBLE);

            // Set click listeners using the universal action handler
            itemContainer.setOnClickListener(v -> listener.onEmailClick(email));

            // Universal delete handler
            buttonDelete.setOnClickListener(v -> {
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailRemoved(email.getId());
//                        actionHandler.showToast("Email deleted successfully");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Delete failed: " + error);
                    }
                });
            });

            // Universal mark as read handler
            buttonMarkRead.setOnClickListener(v -> {
                actionHandler.markAsRead(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailReadStatusChanged(email.getId(), true);
//                        actionHandler.showToast("Marked as read");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as read failed: " + error);
                    }
                });
            });

            // Universal mark as unread handler
            buttonMarkUnread.setOnClickListener(v -> {
                actionHandler.markAsUnread(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailReadStatusChanged(email.getId(), false);
//                        actionHandler.showToast("Marked as unread");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as unread failed: " + error);
                    }
                });
            });

            // Universal spam handler
            buttonSpam.setOnClickListener(v -> {
                actionHandler.markAsSpam(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailRemoved(email.getId());
//                        actionHandler.showToast("Marked as spam");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as spam failed: " + error);
                    }
                });
            });

            // Universal star toggle handler
            buttonStar.setOnClickListener(v -> {
                actionHandler.toggleStar(email.getId(), email.isStarred(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        listUpdateListener.onEmailStarStatusChanged(email.getId(), !email.isStarred());
//                        actionHandler.showToast(email.isStarred() ? "Unstarred" : "Starred");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Star toggle failed: " + error);
                    }
                });
            });

            // Keep the original restore and labels handlers
            buttonRestore.setOnClickListener(v -> listener.onRestoreClick(email));
            buttonLabels.setOnClickListener(v -> listener.onLabelsClick(email));
            buttonSpam.setOnClickListener(v -> listener.onSpamClick(email));
        }
    }

    static class RoundedBackgroundSpan extends ReplacementSpan {
        private final int backgroundColor;
        private final int textColor;
        private final float cornerRadius;
        private final float padding;
        private final float verticalPadding;
        private final float strokeWidth;
        private final int strokeColor;
        public RoundedBackgroundSpan(int backgroundColor, int textColor, float cornerRadius, float padding, float verticalPadding) {
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            this.cornerRadius = cornerRadius;
            this.padding = padding;
            this.verticalPadding = verticalPadding;
            this.strokeWidth = 2f;
            this.strokeColor = textColor;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(paint.measureText(text, start, end) + padding * 2);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            float textWidth = paint.measureText(text, start, end);
            Log.d("RoundedBackgroundSpan", "draw: Text: '" + text.subSequence(start, end) + "', Width: " + textWidth + ", Rect: (" + x + ", " + (top - verticalPadding) + ", " + (x + textWidth + 2 * padding) + ", " + (bottom + verticalPadding) + ")");
            if (textWidth <= 0) {
                Log.w("RoundedBackgroundSpan", "Invalid text width, using fallback");
                textWidth = 1f;
            }
            RectF rect = new RectF(
                    x,
                    top - verticalPadding,
                    x + textWidth + 2 * padding,
                    bottom + verticalPadding
            );
            if (rect.width() <= 0 || rect.height() <= 0) {
                Log.w("RoundedBackgroundSpan", "Invalid RectF, adjusting: " + rect);
                rect.right = rect.left + 1f;
                rect.bottom = rect.top + 1f;
            }
            paint.setColor(backgroundColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
            paint.setColor(strokeColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
            paint.setColor(textColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, start, end, x + padding, y, paint);
        }
    }
//        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
////            RectF rect = new RectF(x, top - verticalPadding, x + paint.measureText(text, start, end) + 2 * padding, bottom + verticalPadding);
////            paint.setColor(backgroundColor);
////            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
////            paint.setColor(textColor);
////            canvas.drawText(text, start, end, x + padding, y, paint);
//            RectF rect = new RectF(
//                    x,
//                    top - verticalPadding,
//                    x + paint.measureText(text, start, end) + 2 * padding,
//                    bottom + verticalPadding
//            );
//
//            // Draw background
//            paint.setColor(backgroundColor);
//            paint.setStyle(Paint.Style.FILL);
//            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
//
//            // Draw thin black outline
//            paint.setColor(strokeColor);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(strokeWidth);
//            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
//
//            // Draw text
//            paint.setColor(textColor);
//            paint.setStyle(Paint.Style.FILL);
//            canvas.drawText(text, start, end, x + padding, y, paint);
//        }
//    }
}