package haaproject.web.olympic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class OlympicAction extends ActionSupport {

	private PlayerInfo player;

	public OlympicAction() {
		player = new PlayerInfo();
	}

	private int playerid;

	List<PlayerInfo> list = new ArrayList<PlayerInfo>();

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public List<PlayerInfo> getAllPlayers() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		list = (List<PlayerInfo>) session.getAttribute("player");
		if (list == null) {
			list = new ArrayList<PlayerInfo>();
			PlayerInfo no1 = new PlayerInfo(2008, "Andy", "man", 25, 1111111111);
			PlayerInfo no2 = new PlayerInfo(2009, "Lily", "female", 25, 22222222);
			PlayerInfo no3 = new PlayerInfo(2010, "Kaka", "man", 25, 1111111111);
			list.add(no1);
			list.add(no2);
			list.add(no3);
			session.setAttribute("player", list);
		}
		return list;
	}

	public List<PlayerInfo> getSelect() {
		return getAllPlayers();
	}

	public String save() {
		list = getAllPlayers();
		list.add(player);
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String viewOne() {
		list = getAllPlayers();
		for (int i = 0; i < list.size(); i++) {
			PlayerInfo p = list.get(i);
			if (p.getId() == playerid) {
				this.player = p;
			}
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String modify() {
		list = getAllPlayers();
		for (int i = 0; i < list.size(); i++) {
			PlayerInfo p = list.get(i);
			if (p.getId() == player.getId()) {
				list.remove(p);
				list.add(player);
				break;
			}
		}
		return SUCCESS;
	}

	public PlayerInfo getPlayer() {
		return player;
	}

	public void setPlayer(PlayerInfo player) {
		this.player = player;
	}

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	public List<PlayerInfo> getList() {
		return list;
	}

	public void setList(List<PlayerInfo> list) {
		this.list = list;
	}
}
