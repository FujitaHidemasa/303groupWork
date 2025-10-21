package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.Item;
import com.example.voidr.repository.ItemMapper;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService
{
	private final ItemMapper itemMapper;

	@Override
	public List<Item> getAllItems()
	{
		return itemMapper.selectAll();
	}

	@Override
	public List<Item> getRangeItems(Integer min, Integer max)
	{
		return itemMapper.selectByRangeId(min, max);
	}

	@Override
	public Item getItemById(Long id)
	{
		return itemMapper.selectById(id);
	}

	@Override
	public void createItem(Item item)
	{
		itemMapper.insert(item);
	}

	@Override
	public void updateItem(Item item)
	{
		itemMapper.update(item);
	}

	@Override
	public void deleteItem(Long id)
	{
		itemMapper.delete(id);
	}

}
