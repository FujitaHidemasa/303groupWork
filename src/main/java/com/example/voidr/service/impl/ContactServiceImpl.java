// ★新規作成：ContactServiceImpl
package com.example.voidr.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Contact;
import com.example.voidr.repository.ContactMapper;
import com.example.voidr.service.ContactService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

	private final ContactMapper contactMapper;

	@Override
	@Transactional
	public void submitContact(Contact contact) {
		// ステータスが未指定なら NEW とする
		if (contact.getStatus() == null || contact.getStatus().isBlank()) {
			contact.setStatus("NEW"); // 未対応
		}
		contactMapper.insert(contact);
	}

	@Override
	public List<Contact> findAll() {
		return contactMapper.findAll();
	}

	@Override
	public List<Contact> findByStatus(String status) {
		if (status == null || status.isBlank()) {
			return contactMapper.findAll();
		}
		return contactMapper.findByStatus(status);
	}

	@Override
	public Contact findById(long id) {
		return contactMapper.findById(id);
	}

	@Override
	@Transactional
	public void updateStatus(long id, String status, String adminReply) {
		LocalDateTime repliedAt = null;
		// 対応済みにしたときだけ repliedAt を入れる例
		if ("RESOLVED".equals(status)) {
			repliedAt = LocalDateTime.now();
		}
		contactMapper.updateStatus(id, status, adminReply, repliedAt);
	}
	
	@Override
	@Transactional
	public void deleteById(long id) {
		contactMapper.deleteById(id);
	}
}
