package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.bean.AddressBook;
import com.itheima.dao.AddressBookDao;
import com.itheima.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookDao dao;

    @Override
    public void save(AddressBook addressBook) {
        dao.insert(addressBook);
    }

    @Override
    public void update(LambdaUpdateWrapper<AddressBook> wrapper) {
        dao.update(null , wrapper);
    }

    @Override
    public void updateById(AddressBook addressBook) {
        dao.updateById(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return dao.selectById(id);
    }

    @Override
    public AddressBook getOne(LambdaQueryWrapper<AddressBook> queryWrapper) {
        return dao.selectOne(queryWrapper);
    }

    @Override
    public List<AddressBook> list(LambdaQueryWrapper<AddressBook> queryWrapper) {
        return dao.selectList(queryWrapper);
    }
}
