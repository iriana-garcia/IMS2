package com.ghw.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "message_board")
public class MessageBoard implements IEntity, Serializable {

	@Id
	@Column(name = "mss_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "mss_date", updatable = false)
	private Date dateMessage = new Date();

	@Column(name = "mss_message", updatable = false)
	private String message;

	@Column(name = "mss_reply_date")
	private Date dateReplyMessage;

	@Column(name = "mss_reply")
	private String replyMessage;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "use_id")
	private User userReply;

	@ManyToOne(targetEntity = Invoice.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "inv_id", nullable = false, updatable = false)
	private Invoice invoice;

	@OneToMany(mappedBy = "messageBoard", fetch = FetchType.LAZY)
	private List<MessageBoardGroup> messageBoardGroups = new ArrayList<MessageBoardGroup>();

	@Transient
	private String fieldAdicional = "";

	public String getDateMessageFormat() {
		return dateMessage != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(dateMessage) : "";
	}

	public String getDateMessageReplyFormat() {
		return dateReplyMessage != null ? (new SimpleDateFormat(
				"E, M-d-yy h:mm a")).format(dateReplyMessage) : "";
	}

	public boolean isReply() {
		return dateReplyMessage != null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateMessage() {
		return dateMessage;
	}

	public void setDateMessage(Date dateMessage) {
		this.dateMessage = dateMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDateReplyMessage() {
		return dateReplyMessage;
	}

	public void setDateReplyMessage(Date dateReplyMessage) {
		this.dateReplyMessage = dateReplyMessage;
	}

	public String getReplyMessage() {
		return replyMessage;
	}

	public void setReplyMessage(String replyMessage) {
		this.replyMessage = replyMessage;
	}

	public User getUserReply() {
		return userReply;
	}

	public void setUserReply(User userReply) {
		this.userReply = userReply;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@Override
	public String getIdentity() {
		return id;
	}

	public String getFieldAdicional() {
		return fieldAdicional;
	}

	public void setFieldAdicional(String fieldAdicional) {
		this.fieldAdicional = fieldAdicional;
	}

	@Override
	public String toString() {
		return "";
	}

	public String getStatusDescription() {
		return dateReplyMessage != null ? "Replied" : "Waiting Reply";
	}

	public List<MessageBoardGroup> getMessageBoardGroups() {
		return messageBoardGroups;
	}

	public void setMessageBoardGroups(List<MessageBoardGroup> messageBoardGroups) {
		this.messageBoardGroups = messageBoardGroups;
	}

}
