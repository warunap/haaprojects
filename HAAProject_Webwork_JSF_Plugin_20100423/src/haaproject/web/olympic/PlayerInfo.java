package haaproject.web.olympic;

public class PlayerInfo {

	private int id;

	private String name;

	private int age;

	private String sex;

	private int tel;

	// 定义默认的构造函数
	public PlayerInfo() {
	}

	// 重写构造函数
	public PlayerInfo(int id, String name, String sex, int age, int tel) {
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.tel = tel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getTel() {
		return tel;
	}

	public void setTel(int tel) {
		this.tel = tel;
	}

}
