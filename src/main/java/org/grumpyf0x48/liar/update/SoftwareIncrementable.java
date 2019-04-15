package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.exceptions.SoftwareException;

interface SoftwareIncrementable<T>
{
    T getNext() throws SoftwareException;
}
