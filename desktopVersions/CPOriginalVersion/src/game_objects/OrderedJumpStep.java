package game_objects;

/**
 * Created by vincenzo on 12/12/2015.
 */

// orderedPath(O,A,B,I,ID,S)
public class OrderedJumpStep {

	private int incomingNode;
	private int srcNode;
	private int dstNode;
	private int order;
	private int id;
	private String eatenPawnStatus;

	public OrderedJumpStep() {
	}

	public OrderedJumpStep(int incomingNode, int srcNode, int dstNode, int order, int id, String eatenPawnStatus) {
		this.incomingNode = incomingNode;
		this.srcNode = srcNode;
		this.dstNode = dstNode;
		this.order = order;
		this.id = id;
		this.eatenPawnStatus = eatenPawnStatus;
	}

	public int getIncomingNode() {
		return incomingNode;
	}

	public void setIncomingNode(int incomingNode) {
		this.incomingNode = incomingNode;
	}

	public int getSrcNode() {
		return srcNode;
	}

	public void setSrcNode(int srcNode) {
		this.srcNode = srcNode;
	}

	public int getDstNode() {
		return dstNode;
	}

	public void setDstNode(int dstNode) {
		this.dstNode = dstNode;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEatenPawnStatus() {
		return eatenPawnStatus;
	}

	public void setEatenPawnStatus(String eatenPawnStatus) {
		this.eatenPawnStatus = eatenPawnStatus;
	}

	@Override
	public String toString() {
		return "orderedJumpStep(" + order + "," + incomingNode + "," + srcNode + "," + dstNode + "," + eatenPawnStatus
				+ ").";
	}
}
