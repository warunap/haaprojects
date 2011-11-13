package org.haaproject.converter.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaproject.converter.dom.Container;
import org.haaproject.converter.dom.Converter;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.util.ParseUtil;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ReadStatus {

	private static final Log LOG = LogFactory.getLog(ReadStatus.class);

	private int batchCount = 0;

	private int lineNum = 0;

	private Converter converter;

	private Container container;

	private Map<String, Object> dataObjectMap = new HashMap<String, Object>();

	private Map<String, Object> rootData = new HashMap<String, Object>();

	private List<String> batchContent;

	/*---------------------------------------------------*/
	public boolean hasNext() {
		return container.getNext() != null;
	}

	public void moveToNextContainer() {
		if (!hasNext()) {
			throw new RuntimeException("No next container!");
		}
		Container next = container.getNext();
		container = next;
		LOG.debug("moveToNextContainer:" + container == null ? "null" : container.getConvertName());
	}

	public Object getReadData() {
		return rootData.get(converter.getComponent().getName());
	}

	public Object getCurrContainerData() throws ParseException {
		return getDataObject(container);
	}

	private Object getDataObject(Container c) throws ParseException {
		Object obj = dataObjectMap.get(c.getConvertName());
		if (obj == null) {
			obj = c.newDataInstance();
			Object parentObj = rootData;
			if (c.getParent() != null) {
				parentObj = getDataObject(c.getParent());
			}
			ParseUtil.setValue(c.getName(), parentObj, obj);

			dataObjectMap.put(c.getConvertName(), obj);
		}
		return obj;
	}

	public void increaseBatchCount() {
		this.batchCount += 1;
	}

	public void increaseLineNum() {
		this.lineNum += 1;
	}

	public void resetData() {
		dataObjectMap = new HashMap<String, Object>();
		rootData = new HashMap<String, Object>();
		lineNum = 0;
	}

	public boolean isBatchOver() {
		return lineNum >= batchContent.size();
	}

	public String getCurrBatchLine() {
		return batchContent.get(lineNum);
	}

	/*---------------------------------------------------*/

	public void setConverter(Converter converter) {
		this.converter = converter;
		if (converter != null) {
			container = converter.getComponent();
		}
	}

	public int getBatchCount() {
		return batchCount;
	}

	public int lineNum() {
		return lineNum;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public void setBatchContent(List<String> list) {
		this.batchContent = list;
		resetData();
	}

	public List<String> getBatchContent() {
		return batchContent;
	}

}
