package com.rc2s.[pluginname].common.vo;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.rc2s.annotations.SourceControl;

@Entity
@SourceControl
public class Bot implements Serializable
{
	@Id
	private Integer id;
}
