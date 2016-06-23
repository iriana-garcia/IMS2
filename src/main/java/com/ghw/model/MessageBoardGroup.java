package com.ghw.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "vw_message_board")
public class MessageBoardGroup implements Serializable {

	@Column(name = "msg_id", length = 36, columnDefinition = "CHAR(36)")
	@Id
	private String id;

	@ManyToOne(targetEntity = MessageBoard.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "mss_id", nullable = false, updatable = false)
	private MessageBoard messageBoard;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MessageBoard getMessageBoard() {
		return messageBoard;
	}

	public void setMessageBoard(MessageBoard messageBoard) {
		this.messageBoard = messageBoard;
	}

}
