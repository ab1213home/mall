package com.jiang.mall.service;

import com.jiang.mall.domain.vo.DirectoryVo;

import java.io.File;
import java.util.List;

public interface IFileService {
	DirectoryVo getFileList(File folder);

	long getFolderSize(File folder);

	int getFileCount(File folder);

	List<String> getFaceTemplateList(File folder);
}
