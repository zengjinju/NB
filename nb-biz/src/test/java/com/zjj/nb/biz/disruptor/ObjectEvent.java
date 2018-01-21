package com.zjj.nb.biz.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by admin on 2017/12/14.
 */
public class ObjectEvent {
	private String id;
	private long price;
	private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static EventFactory<ObjectEvent> FACTORY=new EventFactory<ObjectEvent>() {
		@Override
		public ObjectEvent newInstance() {
			return new ObjectEvent();
		}
	};

	@Override
	public String toString() {
		final StringBuilder builder=new StringBuilder();
		builder.append("{")
				.append("id:").append(getId()).append(",")
				.append("price:").append(getPrice()).append(",")
				.append("value:").append(getValue()).append("}");
		return builder.toString();
	}
}


