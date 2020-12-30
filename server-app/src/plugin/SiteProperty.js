export default class SiteProperty {
  constructor(data) {
    this._data = data;
  }

  isEnum() {
    return this.type === "ENUM";
  }

  get documentation() {
    return this._data.documentation;
  }

  get masked() {
    return this._data.masked;
  }

  get name() {
    return this._data.name;
  }

  get required() {
    return this._data.required;
  }

  get type() {
    return this._data.type;
  }

  get value() {
    return this._value || this._getValue();
  }

  set value(value) {
    this._value = value;
  }

  _getValue() {
    return this._data.value || this._data.variable || this._data.defaultValue;
  }
}
