package com.itheima.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.bean.AddressBook;

import java.util.List;

public interface AddressBookService {

    void save(AddressBook addressBook);

    void update(LambdaUpdateWrapper<AddressBook> wrapper);

    void updateById(AddressBook addressBook);

    AddressBook getById(Long id);

    AddressBook getOne(LambdaQueryWrapper<AddressBook> queryWrapper);

    List<AddressBook> list(LambdaQueryWrapper<AddressBook> queryWrapper);
}
