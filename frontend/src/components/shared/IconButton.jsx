//@flow
import { Button, Icon } from '@blueprintjs/core';
import type { IconName } from '@blueprintjs/icons';
import * as React from 'react';

export type IconButtonProps = {
  icon: IconName,
  title?: string | false | null,
  [string]: any,
}

export const IconButton = ({icon, title, ...props}: IconButtonProps) => {
  if (title) {
    // this works around https://github.com/palantir/blueprint/issues/2321
    const iconWithoutTitle = <Icon icon={icon} title={false} />;
    return <Button {...props} icon={iconWithoutTitle} title={title} />;
  }
  return <Button {...props} icon={icon} />;
};
