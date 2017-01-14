export const SET_USERNAME = 'homePage/SET_USERNAME'

export const setUsername = (userName, displayName, index) => ({
  type: SET_USERNAME,
  userName,
  index,
  displayName
})
