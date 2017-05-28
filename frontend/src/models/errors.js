import { Record, List } from 'immutable';

const ErrorsRecord = Record({
  nextId: 0,
  history: new List(),
});

export default class ErrorsState extends ErrorsRecord {
  addError(error) {
    const errorObject = new Error({ id: this.nextId, error: new ErrorBag(error) });
    return this.set('history', this.history.push(errorObject)).set('nextId', this.nextId + 1);
  }
}

const ErrorRecord = Record({
  id: -1,
  timestamp: new Date(),
  error: new ErrorsRecord(),
});

export class Error extends ErrorRecord {}

const ErrorBagRecord = Record({
  type: '',
  position: 'bottom-left',
  options: {
    icon: 'error',
    removeOnHover: true,
    showCloseButton: true,
  },
  title: 'Unknown Error',
});

export class ErrorBag extends ErrorBagRecord {}
